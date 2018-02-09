package com.arecmetafora.jsdroid.debugger;

/**
 * Debug call frame representation.
 */
class StackFrame {

	/**
	 * Stack frame identifier.
	 */
	private int id;

	/**
	 * Name of the function which scopes this call frame.
	 */
	private String name;

	/**
	 * Name of the file in which the call is being made.
	 */
	private String fileName;

	/**
	 * Line number in which the call is being made.
	 */
	private int line;

	/**
	 * Set of local variables.
	 */
	private Variable[] variables;

	/**
	 * Creates a new stack frame.
	 *
	 * @param id Stack frame identifier.
	 * @param fileName Name of the file in which the call is being made.
	 * @param line Line number in which the call is being made.
	 */
	StackFrame(int id, String name, String fileName, int line) {
		this.id = id;
		this.name = name;
		this.fileName = fileName;
		this.line = line;
	}

	/**
	 * @return The name of the function which scopes this call frame.
	 */
	String getName() {
		return name;
	}

	/**
	 * @return The stack frame identifier.
	 */
	int getId() {
		return this.id;
	}

	/**
	 * @return The line number in which the call is being made.
	 */
	int getLine() {
		return this.line;
	}

	/**
	 * Sets the stack frame variables.
	 *
	 * @param variables The stack frame local variables.
	 */
	void setVariables(Variable[] variables) {
		this.variables = variables;
	}
}
