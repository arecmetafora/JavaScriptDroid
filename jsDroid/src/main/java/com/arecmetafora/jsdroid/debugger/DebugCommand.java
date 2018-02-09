package com.arecmetafora.jsdroid.debugger;

import com.google.gson.annotations.SerializedName;

/**
 * List of all debug commands sent or received by a server.
 */
enum DebugCommand {

	/**
	 * Adds a breakpoint.
	 */
	@SerializedName("AddBreakpoint")
	ADD_BREAKPOINT(Breakpoint.class),

	/**
	 * Removes a breakpoint.
	 */
	@SerializedName("RemoveBreakpoint")
	REMOVE_BREAKPOINT(Breakpoint.class),

	/**
	 * Notifies a breakpoint hit.
	 */
	@SerializedName("BreakpointHit")
	BREAKPOINT_HIT(ThreadInfo.class),

	/**
	 * Notifies an exception hit.
	 */
	@SerializedName("ExceptionHit")
	EXCEPTION_HIT(ThreadInfo.class),

	/**
	 * Notifies an step hit.
	 */
	@SerializedName("StepHit")
	STEP_HIT(ThreadInfo.class),

	/**
	 * Steps in the next line.
	 */
	@SerializedName("StepIn")
	STEP_IN(ThreadInfo.class),

	/**
	 * Steps out from current scope.
	 */
	@SerializedName("StepOut")
	STEP_OUT(ThreadInfo.class),

	/**
	 * Steps over next scope.
	 */
	@SerializedName("StepOver")
	STEP_OVER(ThreadInfo.class),

	/**
	 * Pauses the script execution threads.
	 */
	@SerializedName("Pause")
	PAUSE(ThreadInfo.class),

	/**
	 * Continues to the next breakpoint.
	 */
	@SerializedName("Continue")
	CONTINUE(ThreadInfo.class),

	/**
	 * Requests an expression evaluation.
	 */
	@SerializedName("Evaluate")
	EVALUATE(EvaluateRequest.class),

	/**
	 * Responds an evaluation request.
	 */
	@SerializedName("EvaluateResponse")
	EVALUATE_RESPONSE(EvaluateResponse.class),

	/**
	 * Requests a variable.
	 */
	@SerializedName("VariableRequest")
	VARIABLE_REQUEST(VariableRequest.class),

	/**
	 * Responds an variable request.
	 */
	@SerializedName("VariableResponse")
	VARIABLE_RESPONSE(VariableResponse.class),

	/**
	 * Send a log message.
	 */
	@SerializedName("LogMessage")
	LOG_MESSAGE(LogMessage.class),

	/**
	 * Requests the last SQL executions.
	 */
	@SerializedName("SQLExecutionRequest")
	SQL_EXECUTION_REQUEST(Void.class),

	/**
	 * Send a log message.
	 */
	@SerializedName("Profiling")
	PROFILING(ProfilingInfo.class);

	/**
	 * Type of the class responsible for managing the debug command.
	 */
	private Class<?> type;

	/**
	 * Creates a new Debug command enumerator.
	 *
	 * @param type Type of the class responsible for managing the debug command.
	 */
	DebugCommand(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return The type of the class responsible for managing the debug command.
	 */
	Class<?> getType() {
		return this.type;
	}
}
