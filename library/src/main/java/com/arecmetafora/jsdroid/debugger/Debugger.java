package com.arecmetafora.jsdroid.debugger;

import android.util.SparseArray;

import com.arecmetafora.jsdroid.JavaScriptException;
import com.arecmetafora.jsdroid.JavaScriptDroid;
import com.arecmetafora.jsdroid.annotation.JavaScriptMapped;
import com.arecmetafora.jsdroid.UnmappedObject;
import com.arecmetafora.jsdroid.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to establish connection to a debugger server and control the debug flow.
 */
public final class Debugger implements SocketClient.Listener {

	/**
	 * Debugger instance.
	 */
	private static Debugger instance;

	/**
	 * List of breakpoint hits.
	 */
	private SparseArray<DebuggerContext> breakpointHits = new SparseArray<>();

	/**
	 * List of all active debugger contexts by JavaScript context pointer.
	 */
	private SparseArray<DebuggerContext> contexts = new SparseArray<>();

	/**
	 * Debug client.
	 */
	private SocketClient client;

	/**
	 * @return The current debugger instance.
	 */
	public static synchronized Debugger getInstance() {
		if (instance == null) {
			instance = new Debugger();
		}
		return instance;
	}

	/**
	 * Creates a new debugger.
	 *
	 * @param host The server host name.
	 * @param port The server port.
	 */
	private static void initialize(String host, int port) {
		if (instance.isRunning()) {
			// Only stops the debug if either server or port has changed.
			if ((Utils.isNotEquals(host, instance.client.getHost()) || port != instance.client
				.getPort())) {
				instance.stop();
			} else {
				// Otherwise, does nothing.
				return;
			}
		}
		instance.client = new SocketClient(host, port, instance);
	}

	/**
	 * Adds a breakpoint.
	 *
	 * @param breakpoint Breakpoint to be added.
	 */
	private void addBreakpoint(Breakpoint breakpoint) {
		for(int i = 0; i < contexts.size(); i++) {
			addBreakpoint(contexts.keyAt(i), breakpoint);
		}
	}

	/**
	 * Adds a breakpoint to a context.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 * @param breakpoint Breakpoint to be added.
	 */
	private void addBreakpoint(int jsContextPointer, Breakpoint breakpoint) {
		DebuggerContext context = contexts.get(jsContextPointer);

		int sourceId = context.getSourceIdByFileName(breakpoint.getFileName());
		if(sourceId >= 0) {
			int breakpointID = setBreakpoint(jsContextPointer, sourceId,
					breakpoint.getLine() - 1, breakpoint.getColumn(),
					Utils.defaultIfNull(breakpoint.getCondition(), ""), breakpoint.getHitCount());
			breakpoint.setId(breakpointID);
			context.getBreakpoints().add(breakpoint);
		}
	}

	/**
	 * Breaks the JavaScript execution.
	 */
	private void breakProgram() {
		for(int i = 0; i < contexts.size(); i++) {
			breakProgram(contexts.keyAt(i));
		}
	}

	/**
	 * Attachs the client to a debug server.
	 *
	 * @param hostName Network address of the debug server.
	 * @param port Network port of the debug server.
	 */
	@JavaScriptMapped
	public void attach(String hostName, int port) {
		Debugger.initialize(hostName, port);
		if (!isRunning()) {
			start();
		}
	}

	/**
	 * Forces a debug breakpoint hit.
	 */
	@JavaScriptMapped
	public void debug() {
	}

	/**
	 * Evaluates an expression and send the result to the debug server.
	 *
	 * @param expression The expression to be evaluated.
	 * @param thread The thread in which the script will be evaluated.
	 * @param requestId Id of the message request.
	 */
	private void evaluateExpression(String expression, ThreadInfo thread, int requestId) {
		if (thread == null) {
			return;
		}

		EvaluateResponse response;

		try {

			Object result = evaluateScript(thread.getId(), expression);
			String evaluationResult = getVariableDescription(thread,
					"(" + expression + ")", result);

			response = new EvaluateResponse(evaluationResult, true);

		} catch (JavaScriptException ex) {

			response = new EvaluateResponse(ex.getMessage(), false);
		}

		try {
			// Send the evaluation response to the server
			respondDebugMessage(DebugCommand.EVALUATE_RESPONSE, response, requestId);
		} catch (IOException ex) {
			Utils.log(ex);
			this.stop();
		}
	}

	/**
	 * Parses a stack trace to a list of stack frames.
	 *
	 * @param stackTrace JavaScript call stack.
	 * @return A list of stack frames for the given stack trace.
	 */
	private StackFrame[] getStackFrame(String stackTrace) {

		LinkedList<StackFrame> stackFrameList = new LinkedList<>();

		Matcher matcher = Pattern.compile("#(\\d+)\\s*(\\w*).*at\\s+(.*):(\\d+)",
			Pattern.CASE_INSENSITIVE).matcher(stackTrace);

		while (matcher.find()) {
			int stackId = Integer.parseInt(matcher.group(1));
			String name = matcher.group(2);
			String fileName = matcher.group(3);
			int line = Integer.parseInt(matcher.group(4));

			// Check if the stack trace element doesn't represents the anonymous
			// function created to execute events, like onTap
			// (evaluateScriptWithParameters)
			if (!Utils.isNullOrEmpty(fileName)) {
				stackFrameList.add(new StackFrame(stackId, name, fileName, line));
			}
		}

		return stackFrameList.toArray(new StackFrame[0]);
	}

	/**
	 * Fill the stack frame variables.
	 *
	 * @param thread The thread in which the script will be evaluated.
	 */
	private void fillStackFrameVariables(ThreadInfo thread, StackFrame stackTrace)
			throws JavaScriptException {

		Object[] propertyNames = getPropertyNamesOfCurrentScope(thread.getId());

		List<String> functionParams = new LinkedList<>();

		// Sorting all property members
		LinkedList<Object> propertyList = new LinkedList<>(Arrays.asList(propertyNames));
		Collections.sort(propertyList, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}
		});

		boolean containsThis = propertyList.contains("this");
		if(containsThis) {
			propertyList.remove("this");
		}

		boolean containsArguments = propertyList.contains("arguments");
		if(containsArguments) {
			propertyList.remove("arguments");

			double argumentsLength = (Double) evaluateScript(thread.getId(), "arguments.length");
			for (int i = 0; i < argumentsLength; i++) {
				for(Object property : propertyNames) {
					String propertySrt = property.toString();
					boolean isArgument = (Boolean) evaluateScript(thread.getId(),
							"arguments[" + i + "] === " + propertySrt);
					if(isArgument) {
						// Remove param and insert at the top of the list
						propertyList.remove(propertySrt);
						propertyList.add(i, propertySrt);
						functionParams.add(propertySrt);
					}
				}
			}
		}

		// Add this at the top of the param list
		propertyList.add(0, "this");

		ArrayList<Variable> variables = new ArrayList<>(propertyList.size());

		for(Object property : propertyList) {
			String propertyName = property.toString();

			boolean isArgument = functionParams.contains(propertyName);

			Object value = evaluateScript(thread.getId(), propertyName);
			String propertyValue = getVariableDescription(thread, propertyName, value);

			variables.add(new Variable(propertyName, propertyValue, isArgument));
		}

		stackTrace.setVariables(variables.toArray(new Variable[0]));
	}

	/**
	 * Gets a readable description of a evaluated variable.
	 *
	 * @param thread The thread which the variable is being requested.
	 * @param variable The name of the variable to be described.
	 * @param value The variable value.
	 * @return The description of the object variable.
	 */
	private String getVariableDescription(ThreadInfo thread, String variable, Object value) {
		if (value == null) {
			return "<null>";
		} else {
			if(value instanceof UnmappedObject) {
				try {
					String script = variable + ".constructor.name";
					value = evaluateScript(thread.getId(), script);
					return String.format("[object %s]", value);
				} catch (JavaScriptException ex) {
					Utils.log(ex);
				}
			}
			return getToStringDescription(value);
		}
	}

	/**
	 * Handles a breakpoint hit.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 */
	public void handleBreakpointHit(int jsContextPointer) {
		handleBreak(jsContextPointer, null);
	}

	/**
	 * Handles an exception hit.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * @param exception Exception that caused the breakpoint or null if was caused by a breakpoint hit
	 * executed.
	 */
	public void handleExceptionHit(int jsContextPointer, Throwable exception) {
		handleBreak(jsContextPointer, exception);
	}

	/**
	 * Handles a breakpoint hit.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * @param exception Exception that caused the breakpoint or null if was caused by a breakpoint hit
	 * executed.
	 */
	private void handleBreak(int jsContextPointer, Throwable exception) {
		if (!isRunning()) {
			return;
		}

		StackFrame[] stackFrame = getStackFrame(JavaScriptDroid
			.getJavaScriptStackTrace(jsContextPointer));

		if (stackFrame.length == 0) {
			return;
		}

		String message = null;
		DebugCommand command = DebugCommand.BREAKPOINT_HIT;
		if(exception != null) {
			message = exception.getMessage();
			command = DebugCommand.EXCEPTION_HIT;
		}

        ThreadInfo thread = new ThreadInfo(jsContextPointer, message, stackFrame);

		try {
			fillStackFrameVariables(thread, stackFrame[0]);
		} catch (JavaScriptException ex) {
			Utils.log(ex);
		}

		performBreakpointHit(thread, command);
	}

	/**
	 * @return Whether the debugger is running
	 */
	private boolean isRunning() {
		return this.client != null && this.client.isConnected();
	}

	/**
	 * Writes a console message to the log file.
	 *
	 * @param message Console message.
	 * @param source String identifying the source of the message.
	 * @param details Additional details.
	 */
	@JavaScriptMapped
	public void log(String message, String source, String details) {
		this.log(new LogMessage(LogMessage.LogType.CONSOLE.getValue(), message, source, details));
	}

	/**
	 * Creates a log message response.
	 *
	 * @param logMessage Log message to send.
	 */
	private void log(LogMessage logMessage) {
		if (!isRunning()) {
			return;
		}

		try {
			this.sendDebugMessage(DebugCommand.LOG_MESSAGE, logMessage);
		} catch (IOException ex) {
			Utils.log(ex);
			this.stop();
		}
	}

	@Override
	public void onConnectionEstablished() {

		if (!isAttached(JavaScriptDroid.getJsGlobalContexPointer())) {
			contexts.append(JavaScriptDroid.getJsGlobalContexPointer(), new DebuggerContext());
			attach(JavaScriptDroid.getJsGlobalContexPointer());
		}
	}

	@Override
	public void onConnectionLost() {
		resetDebuggerState();
	}

	@Override
	public void onMessageReceived(String message) {
		Gson gson = new GsonBuilder().create();

		ProtocolMessage debugMessage = gson.fromJson(message,
				ProtocolMessage.class);

		Object arg = debugMessage.getArguments();
		ThreadInfo thread;

		switch (debugMessage.getCommand()) {

			case ADD_BREAKPOINT:
				addBreakpoint((Breakpoint) arg);
				break;

			case REMOVE_BREAKPOINT:
				removeBreakpoint((Breakpoint) arg);
				break;

			case STEP_IN:
				thread = (ThreadInfo) arg;
				stepIntoStatement(thread.getId());
				releaseBreakpointHit(thread);
				break;

            case STEP_OUT:
				thread = (ThreadInfo) arg;
                stepOutOfFunction(thread.getId());
				releaseBreakpointHit(thread);
                break;

            case STEP_OVER:
				thread = (ThreadInfo) arg;
                stepOverStatement(thread.getId());
				releaseBreakpointHit(thread);
                break;

			case CONTINUE:
				thread = (ThreadInfo) arg;
				continueProgram(thread.getId());
				releaseBreakpointHit(thread);
				break;

            case PAUSE:
                breakProgram();
                break;

			case EVALUATE:
			case VARIABLE_REQUEST:
				thread = ((ThreadMessage) arg).getThreadInfo();
				DebuggerContext context = this.contexts.get(thread.getId());
				context.putMessage(debugMessage);
				synchronized (context) {
					context.notifyAll();
				}
				return;

			default:
				break;
		}
	}

	/**
	 * Handles a thread related debug message.
	 *
	 * @param threadInfo The thread in which the debug message will be evaluated.
	 * @param message The message to be handled.
	 */
	private void onThreadMessageReceived(ThreadInfo threadInfo, ProtocolMessage message) {

		switch (message.getCommand()) {

			case EVALUATE:
				EvaluateRequest evaluateRequest = (EvaluateRequest) message.getArguments();
				evaluateExpression(evaluateRequest.getExpression(), threadInfo,
						message.getId());
				break;

			case VARIABLE_REQUEST:
				VariableRequest variableRequest = (VariableRequest) message.getArguments();
				requestVariableAttributes(variableRequest.getVariableName(), threadInfo,
						message.getId());
		}
	}

	/**
	 * Stops a given thread because of a breakpoint hit.
	 *
	 * @param thread Thread which excecution will be stopped by a breakpoint hit.
	 * @param command Debug command to be fired when the breakpoint is hit.
	 */
	private void performBreakpointHit(ThreadInfo thread, DebugCommand command) {

		try {
			// Send a breakpoint hit message to the server
			sendDebugMessage(command, thread);

			DebuggerContext context = this.contexts.get(thread.getId());
			this.breakpointHits.put(thread.getId(), context);

			// Waits until the breakpoint is released by server
			while (this.breakpointHits.indexOfKey(thread.getId()) >= 0) {
				synchronized (context) {
					try {
						context.wait();

						// Run loop to execute messages inside the
						// waiting thread context
						ProtocolMessage message = context.nextDebuggerMessage();
						if(message != null && message.getArguments() instanceof ThreadMessage) {
							onThreadMessageReceived(thread, message);
						}

					} catch (InterruptedException ignored) {
					}
				}
			}

		} catch (IOException ex) {
			Utils.log(ex);
			this.stop();
		}
	}

	/**
	 * Releases a waiting thread that was stopped by a breakpoint hit.
	 *
	 * @param thread The thread that should be released.
	 */
	private void releaseBreakpointHit(ThreadInfo thread) {
		DebuggerContext context = this.contexts.get(thread.getId());
		this.breakpointHits.remove(thread.getId());
		synchronized (context) {
			context.notifyAll();
		}
	}

	/**
	 * Removes a breakpoint.
	 *
	 * @param breakpoint Breakpoint to be removed.
	 */
	private void removeBreakpoint(Breakpoint breakpoint) {
		for(int i = 0; i < contexts.size(); i++) {
			removeBreakpoint(contexts.keyAt(i), breakpoint);
		}
	}

	/**
	 * Removes a breakpoint from a JavaScript context.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 * @param breakpoint Breakpoint to be removed.
	 */
	private void removeBreakpoint(int jsContextPointer, Breakpoint breakpoint) {
		DebuggerContext context = contexts.get(jsContextPointer);

		// The breakpoint parameter came from server and could not
		// have the breakpoint id. So, let's remap this to the in-memory breakpoins.
		int breakpointIndex = context.getBreakpoints().indexOf(breakpoint);

		if(breakpointIndex >= 0) {
			breakpoint = context.getBreakpoints().remove(breakpointIndex);
			removeBreakpoint(jsContextPointer, breakpoint.getId());
		}
	}

	/**
	 * Evaluates an expression and send the result to the debug server.
	 *
	 * @param variableName Name of the variable which attributes are being requested.
	 * @param thread Thread
	 * @param requestId Id of the message request.
	 */
	private void requestVariableAttributes(String variableName,
		ThreadInfo thread, int requestId) {
		if (thread == null) {
			return;
		}

		VariableResponse response;

		try {

			String script = variableName + ".isMappedObject";
			Boolean isMappedObject = (Boolean) evaluateScript(thread.getId(), script);
			if (isMappedObject == null) {
				isMappedObject = false;
			}

			LinkedList<Variable> variables = new LinkedList<>();
			Object[] propertyNames;

			// Checks if the object which properties are being dumped is a mapped object
			if (isMappedObject) {
				// Dump the object properties
				Object jsObj = evaluateScript(thread.getId(), variableName);
				propertyNames = JavaScriptDroid.getMappedProperties(jsObj.getClass());

			} else {
				// Dump all variable names of a regular JavaScript Object
				script = "Object.keys(" + variableName + ");";
				propertyNames = (Object[]) evaluateScript(thread.getId(), script);
			}

			// Dump all variable values
			for (Object it : propertyNames) {
				String propertyName = (String) it;

				if (propertyName.matches("\\d+")) {
					// Parameter is a index
					script = variableName + "[" + propertyName + "]";
				} else {
					// Parameter is a property name
					script = variableName + "." + propertyName;
				}

				Object value = evaluateScript(thread.getId(), script);
				String propertyValue = getVariableDescription(thread, script, value);

				variables.add(new Variable(propertyName, propertyValue));
			}

			response = new VariableResponse(variables.toArray(new Variable[0]),
				thread);

		} catch (JavaScriptException ex) {
			Utils.log(ex);
			this.stop();
			return;
		}

		try {
			// Send the evaluation response to the server
			respondDebugMessage(DebugCommand.VARIABLE_RESPONSE, response, requestId);
		} catch (IOException ex) {
			Utils.log(ex);
			this.stop();
		}
	}

	/**
	 * Resets the debugger to its initial state.
	 */
	private void resetDebuggerState() {

		for(int i = 0; i < contexts.size(); i++) {
			int jsContextPointer = contexts.keyAt(i);

			clearBreakpoints(jsContextPointer);

			// Release breakpoint hit, if exists
			DebuggerContext context = this.breakpointHits.get(jsContextPointer);
			if(context != null) {
				this.breakpointHits.remove(jsContextPointer);
				synchronized (context) {
					context.notifyAll();
				}
			}

			if(isAttached(jsContextPointer)) {
				detach(jsContextPointer);
			}
		}

		contexts.clear();
	}

	/**
	 * Sends a message to the debug server.
	 *
	 * @param command The debug command.
	 * @param arguments The command arguments.
	 */
	private void sendDebugMessage(DebugCommand command, Object arguments)
			throws IOException {
		respondDebugMessage(command, arguments, -1);
	}

	/**
	 * Sends a message to the debug server as a response from a request message.
	 *
	 * @param command The debug command.
	 * @param arguments The command arguments.
	 * @param requestId Id of the message request.
	 */
	private void respondDebugMessage(DebugCommand command, Object arguments, int requestId)
			throws IOException {
		Gson gson = new GsonBuilder().create();

		ProtocolMessage message = new ProtocolMessage(command, arguments, requestId);
		String jsonMessage = gson.toJson(message);

		this.client.sendMessage(jsonMessage);
	}

	/**
	 * Creates a relationship between source names and ids (parsed by JavaScript engine)
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * @param sourceUrl The JavaScript file name.
	 * @param sourceID The JavaScript source identifier.
	 */
	void sourceParsed(int jsContextPointer, String sourceUrl, int sourceID) {
		this.contexts.get(jsContextPointer).getSources().put(sourceUrl, sourceID);
	}

	/**
	 * Starts the debugger.
	 */
	public void start() {
		try {
			this.client.connect();
		} catch (Exception ex) {
			this.client.disconnect();
		}
	}

	/**
	 * Stops the debugger.
	 */
	public void stop() {
		this.client.disconnect();
	}

	/**
	 * Starts the JavaScript profiler.
	 */
	public void startProfiler() {

		if (isRunning()) {
			for (int i = 0; i < contexts.size(); i++) {
				int jsContextPointer = contexts.keyAt(i);
				detach(jsContextPointer);
				startProfiler(jsContextPointer);
			}
		}
	}

	/**
	 * Stops the JavaScript profiler.
	 */
	public void stopProfiler() {

		if (isRunning()) {

			String scriptProfiling = "[";
			for(int i = 0; i < contexts.size(); i++) {
				int jsContextPointer = contexts.keyAt(i);

				scriptProfiling += stopProfiler(jsContextPointer);
				if(i < contexts.size() - 1) {
					scriptProfiling += ",";
				}

				attach(jsContextPointer);
			}
			scriptProfiling += "]";

			ProfilingInfo profilingInfo = new ProfilingInfo(scriptProfiling);

			try {
				sendDebugMessage(DebugCommand.PROFILING, profilingInfo);
			} catch (IOException e) {
				Utils.log(e);
				this.stop();
			}
		}
	}

	/**
	 * Gets the description of the toString or valueOf method execution.
	 *
	 * @param object The object whose methods are being called
	 * @return The toString or valueOf result
	 */
	private String getToStringDescription(Object object) {
		String objectDescription;
		if (object instanceof UnmappedObject) {
			objectDescription = "JavaScript";
		} else if (object instanceof Object[]) {
			objectDescription = "Array";
		} else if (object instanceof GregorianCalendar) {
			return Utils.convertToDateTimeString((GregorianCalendar) object);
		} else if (object instanceof Number || object instanceof String
				|| object instanceof Boolean) {
			return object.toString();
		} else {
			objectDescription = object.getClass().getSimpleName();
		}

		// Default object name format.
		return String.format("[object %s]", objectDescription);
	}

	/**
	 * Attaches the debugger to a context.
	 *
	 * @param jsContextPointer The context in which the debugger will be attached.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void attach(int jsContextPointer);

	/**
	 * Detaches the debugger from a context.
	 *
	 * @param jsContextPointer The context in which the debugger will be detached.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void detach(int jsContextPointer);

	/**
	 * Checks if the debugger is attached to a context.
	 *
	 * @param jsContextPointer The context in which the debugger is detached to.
	 *
	 * @return Whether the debugger is attached to the context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native boolean isAttached(int jsContextPointer);

	/**
	 * Sets a debugger breakpoint.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 * @param sourceID The JavaScript source.
	 * @param line The breakpoint line number.
	 * @param column The breakpoint column number.
	 * @param condition A JavaScript expression representing the breakpoint condition.
	 * @param hitCount Number of times the breakpoint should be hit before breaking execution.
	 *
	 * @return The breakpoint identifier.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native int setBreakpoint(int jsContextPointer, int sourceID, int line, int column, String condition, int hitCount);

	/**
	 * Removes a debugger breakpoint.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 * @param breakpointID The breakpoint identifier.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void removeBreakpoint(int jsContextPointer, int breakpointID);

	/**
	 * Clear all breakpoints.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void clearBreakpoints(int jsContextPointer);

	/**
	 * Breaks the JavaScript execution.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void breakProgram(int jsContextPointer);

	/**
	 * Continues the JavaScript execution.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void continueProgram(int jsContextPointer);

	/**
	 * Proceed the debugging to the next statement, into a statement.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void stepIntoStatement(int jsContextPointer);

	/**
	 * Proceed the debugging to the next statement, over the current statement.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void stepOverStatement(int jsContextPointer);

	/**
	 * Proceed the debugging to the next statement, out of the current scope.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native void stepOutOfFunction(int jsContextPointer);

	/**
	 * Evaluates a JavaScript expression under the current debugging context.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 * @param script The JavaScript to be evaluated.
	 *
	 * @return The result of the JavaScript evaluation.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native Object evaluateScript(int jsContextPointer, String script) throws JavaScriptException;

	/**
	 * Gets the name of all properties from current context.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 *
	 * @return The property names of the current debugger scope.
	 */
	@SuppressWarnings("JniMissingFunction")
	private static native Object[] getPropertyNamesOfCurrentScope(int jsContextPointer);

	/**
	 * Starts the JavaScript profiler.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	static native void startProfiler(int jsContextPointer);

	/**
	 * Stops the JavaScript profiler.
	 *
	 * @param jsContextPointer A pointer to the JavaScript context.
	 *
	 * @return A JSON String containing the profiling data.
	 */
	@SuppressWarnings("JniMissingFunction")
	static native String stopProfiler(int jsContextPointer);
}
