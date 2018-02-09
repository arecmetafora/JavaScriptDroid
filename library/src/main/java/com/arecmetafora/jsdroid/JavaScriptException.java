package com.arecmetafora.jsdroid;

/**
 * Exceptions thrown by the JavaScriptDroid.
 */
public class JavaScriptException extends Exception {

	/**
	 * JavaScript stack trace at the moment of the execution.
	 */
	private String javaScriptStackTrace;

	/**
	 * Creates a new Javascript exception.
	 *
	 * @param message The message thrown by a JavaScript execution.
	 */
	JavaScriptException(String message) {
		super(message);
		javaScriptStackTrace = JavaScriptDroid.getJavaScriptStackTrace();
	}

	/**
	 * Creates a new Javascript exception.
	 *
	 * @param message The message thrown by a JavaScript execution.
	 * @param javaScriptStackTrace JavaScript stack trace at the moment of the execution.
	 */
	JavaScriptException(String message, String javaScriptStackTrace) {
		super(message);
		this.javaScriptStackTrace = javaScriptStackTrace;
	}

	/**
	 * @return the JavaScript stack trace at the moment of the exception thrown.
	 */
	public String getJavaScriptStackTrace() {
		return javaScriptStackTrace;
	}
}
