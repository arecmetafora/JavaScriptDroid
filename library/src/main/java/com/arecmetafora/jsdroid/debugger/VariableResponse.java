package com.arecmetafora.jsdroid.debugger;

/**
 * Debug variable response.
 */
class VariableResponse {

	/**
	 * List of variables for the first depth of the variable that was requested.
	 */
	private Variable[] variables;

	/**
	 * Thread in which the variable is being requested.
	 */
	private ThreadInfo threadInfo;

	/**
	 * Creates a new debug variable response.
	 *
	 * @param variables List of variables for the first depth of the variable that was requested.
	 * @param threadInfo Thread in which the variable is being requested.
	 */
	VariableResponse(Variable[] variables, ThreadInfo threadInfo) {
		this.variables = variables;
		this.threadInfo = threadInfo;
	}

	/**
	 * @return Thread in which the variable is being requested.
	 */
	ThreadInfo getThreadInfo() {
		return this.threadInfo;
	}

	/**
	 * @return The list of variables for the first depth of the variable that was requested.
	 */
	Variable[] getVariables() {
		return this.variables;
	}
}
