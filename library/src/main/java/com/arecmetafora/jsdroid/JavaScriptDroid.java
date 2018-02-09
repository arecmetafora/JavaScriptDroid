package com.arecmetafora.jsdroid;

import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Centers all functionalities of JavaScriptDroid library.
 */
public final class JavaScriptDroid {

	/**
	 * All registered JavaScript classes by its name.
	 */
	private static HashMap<String, APIClass> apiClassesByName = new HashMap<>();

	/**
	 * Registered JavaScript classes by the mapped native class.
	 */
	private static HashMap<Class<?>, APIClass> apiClassesByClass = new HashMap<>();

	/**
	 * Pointer to the global JavaScript context. This context is used in regular scenarios (event
	 * scripts execution). Worker threads must use a separate context, since it can be executed at
	 * the same time of other context executions.
	 */
	private static int jsGlobalContexPointer;

	/**
	 * Private constructor.
	 */
	private JavaScriptDroid() {
	}

	static {
		// Load .so libraries
		System.loadLibrary("js");
		System.loadLibrary("jsdroid");

		// Creates a global JavaScript context.
		jsGlobalContexPointer = createJavaScriptContext();
	}

	/**
	 * Register a class in the global JavaScript context.
	 *
	 * @param clazz Class to be registered.
	 */
	public static void registerClass(Class<?> clazz) {
		APIClass jsClass = new APIClass(clazz);

		apiClassesByName.put(jsClass.getJSName(), jsClass);
		apiClassesByClass.put(clazz, jsClass);

		int jsClassRef = registerJavaScriptClass(jsClass.getJSName(), jsGlobalContexPointer);
		jsClass.setJSClassRefPointer(jsClassRef);
	}

	/**
	 * Register a class in a JavaScript context.
	 *
	 * @param jsName The name of the class to be registered.
	 * @param jsContextPointer The pointer to the JavaScript context which the class is being registered to.
	 *
	 * @return The reference to the JavaScript class definition, so this class can be instantiated
	 * later by JavaScript engine execution.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native int registerJavaScriptClass(String jsName, int jsContextPointer);

	/**
	 * Evaluates a JavaScript.
	 *
	 * @param script The script to be evaluated.
	 *
	 * @return The result of the evaluation.
	 */
	public static Object evaluateScript(String script)
		throws JavaScriptException {
		return evaluateScript(script, "", jsGlobalContexPointer);
	}

	/**
	 * Evaluates a JavaScript retrieved from a file.
	 *
	 * @param script The script to be evaluated.
	 * @param fileName The name of the file which this script was retrieved.
	 *
	 * @return The result of the evaluation.
	 */
	static Object evaluateScript(String script, String fileName)
			throws JavaScriptException {
		return evaluateScript(script, fileName, jsGlobalContexPointer);
	}

	/**
	 * Evaluates a JavaScript.
	 *
	 * @param script The script to be evaluated.
	 * @param fileName The name of the file which this script was retrieved.
	 * @param jsContextPointer The context which the script will be executed.
	 *
	 * @return The result of the evaluation.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native Object evaluateScript(String script, String fileName, int jsContextPointer)
			throws JavaScriptException;

	/**
	 * Evaluates an anonymous JavaScript function, which arguments are supplied by parameters.
	 *
	 * @param script A JavaScript to be executed.
	 * @param paramsNames The name of function arguments.
	 * @param paramsValues The argument values.
	 *
	 * @return The result of the evaluation.
	 */
	public static Object evaluateScriptWithParameters(String script, String[] paramsNames,
			Object... paramsValues) throws JavaScriptException {

		for(Object param : paramsValues) {
			validateParameters(param);
		}

		return evaluateScriptWithParameters(script, paramsNames, paramsValues,
				jsGlobalContexPointer);
	}

	/**
	 * Evaluates an anonymous JavaScript function, which arguments are supplied by parameters.
	 *
	 * @param script A JavaScript to be executed.
	 * @param paramsNames The name of function arguments.
	 * @param paramsValues The argument values.
	 * @param jsContextPointer The context which the script will be executed.
	 *
	 * @return The result of the evaluation.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native Object evaluateScriptWithParameters(String script, String[] paramsNames,
			Object[] paramsValues, int jsContextPointer) throws JavaScriptException;

	/**
	 * Validates the evaluate parameters
	 *
	 * @param param The parameters to be evaluated.
	 */
	private static void validateParameters(Object param) throws JavaScriptClassUnregistered {
		if(param != null) {
			if(param instanceof Number || param instanceof Boolean || param instanceof String ||
					param instanceof GregorianCalendar) {
				// Primitives and dates are valid
				return;
			} else if(param instanceof Object[]) {
				for(Object o : (Object[])(param)) {
					validateParameters(o);
				}
			} else if (getAPIClassByClass(param.getClass()) == null) {
				throw new JavaScriptClassUnregistered(param.getClass());
			}
		}
	}

	/**
	 * Gets the name of all properties mapped for a given class.
	 *
	 * @param clazz The class to be checked.
	 *
	 * @return An array containing a list of properties mapped for the given class.
	 */
	public static String[] getMappedProperties(Class<?> clazz) {
		APIClass apiClass = getAPIClassByClass(clazz);
		Enumeration<APIProperty> properties = apiClass.getProperties();
		LinkedList<String> propertyNamesList = new LinkedList<>();
		while (properties.hasMoreElements()) {
			APIProperty property = properties.nextElement();
			propertyNamesList.add(property.getJSName());
		}

		return propertyNamesList.toArray(new String[0]);
	}

	/**
	 * @return the pointer to the global JavaScript context.
	 */
	public static int getJsGlobalContexPointer() {
		return jsGlobalContexPointer;
	}

	/**
	 * Gets the JavaScript mapped class for a given name.
	 *
	 * @param name The name of the JavaScript class.
	 *
	 * @return The mapped JavaScript class.
	 */
	static APIClass getAPIClassByName(String name) {
		return apiClassesByName.get(name);
	}

	/**
	 * Gets the JavaScript mapped class for a given Java class.
	 *
	 * @param clazz The Java class of the JavaScript class.
	 *
	 * @return The mapped JavaScript class.
	 */
	static APIClass getAPIClassByClass(Class<?> clazz) {
		return apiClassesByClass.get(clazz);
	}

	/**
	 * Create a new JavaScript context.
	 *
	 * @return The pointer to the created JavaScript context.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native int createJavaScriptContext();

	/**
	 * Releases a JavaScript context.
	 *
	 * @param jsContextPointer The JavaScript context to be released.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native void releaseJavaScriptContext(int jsContextPointer);

	/**
	 * Call the JavaScript garbage collector execution.
	 */
	public static void garbageCollect() throws JavaScriptException {
		garbageCollect(jsGlobalContexPointer);
	}

	/**
	 * Call the JavaScript garbage collector execution in specific context.
	 *
	 * @param jsContextPointer The JavaScript context which the objects will be collected.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static synchronized native void garbageCollect(int jsContextPointer)
			throws JavaScriptException;

	/**
	 * Gets the stack trace of the current JavaScript execution from global context.
	 *
	 * @return The stack trace of the current JavaScript execution.
	 */
	public static String getJavaScriptStackTrace() {
		return getJavaScriptStackTrace(getJsGlobalContexPointer());
	}

	/**
	 * Gets the stack trace of the current JavaScript execution of a given context.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which the stack trace will be
	 * obtained.
	 * @return The stack trace of the current JavaScript execution.
	 */
	@SuppressWarnings("JniMissingFunction")
	public static native String getJavaScriptStackTrace(int jsContextPointer);
}
