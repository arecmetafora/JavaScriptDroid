package com.arecmetafora.jsdroid.debugger;

/**
 * Debug variable request.
 */
class VariableRequest extends ThreadMessage {

	/**
	 * Name of the variable which attributes are being requested.
	 */
	private String variableName;

	/**
	 * Creates a new debug variable request.
	 *
	 * @param variableName Name of the variable which attributes are being requested.
	 * @param threadInfo Thread in which the variable is being requested.
	 */
	VariableRequest(String variableName, ThreadInfo threadInfo) {
		super(threadInfo);
		this.variableName = variableName;
	}

	/**
	 * @return The name of the variable which attributes are being requested.
	 */
	String getVariableName() {
		return this.variableName;
	}
}
