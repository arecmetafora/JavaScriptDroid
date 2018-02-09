package com.arecmetafora.jsdroid.debugger;

/**
 * Debug variable representation.
 */
class Variable {

	/**
	 * Name of the variable.
	 */
	private String name;

	/**
	 * Value of the variable.
	 */
	private String value;

	/**
	 * Whether the variable is a function argument.
	 */
	private boolean isArgument;

	/**
	 * Creates a new variable.
	 *
	 * @param name Name of the variable.
	 * @param value Value of the variable.
	 * @param isArgument Whether the variable is a function argument.
	 */
	Variable(String name, String value, boolean isArgument) {
		this.name = name;
		this.value = value;
		this.isArgument = isArgument;
	}

	/**
	 * Creates a new variable.
	 *
	 * @param name Name of the variable.
	 * @param value Value of the variable.
	 */
	Variable(String name, String value) {
		this(name, value, false);
	}
}
