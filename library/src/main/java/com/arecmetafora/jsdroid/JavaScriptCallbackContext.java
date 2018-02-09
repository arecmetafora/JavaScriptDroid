package com.arecmetafora.jsdroid;

/**
 * Contexts of JavaScript callback executions.
 */
enum JavaScriptCallbackContext {

	/**
	 * Represents a context of the execution of a setter operation.
	 */
	GETTER("Getter"),

	/**
	 * Represents a context of the execution of a getter operation.
	 */
	SETTER("Setter"),

	/**
	 * Represents a context of the execution of a method operation.
	 */
	METHOD("Method"),

	/**
	 * Represents a context of the execution of a construction operation.
	 */
	CONSTRUCTOR("Constructor");

	/**
	 * The contextual value of the execution context.
	 */
	private final String context;

	/**
	 * Creates the vertical alignment.
	 *
	 * @param context the contextual value of the execution context.
	 */
	JavaScriptCallbackContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return this.context;
	}
}
