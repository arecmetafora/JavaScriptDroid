package com.arecmetafora.jsdroid.debugger;

/**
 * Debug evaluate response.
 */
class EvaluateResponse {

	/**
	 * Evaluation result.
	 */
	private String value;

	/**
	 * Whether the evaluation was performed successfully.
	 */
	private boolean success;

	/**
	 * Creates a debug evaluate response.
	 *
	 * @param value Evaluation result.
	 * @param success Whether the evaluation was performed successfully.
	 */
	EvaluateResponse(String value, boolean success) {
		this.value = value;
		this.success = success;
	}

	/**
	 * @return The evaluation result.
	 */
	String getValue() {
		return this.value;
	}

	/**
	 * @return Whether the evaluation was performed successfully.
	 */
	boolean isSuccess() {
		return this.success;
	}
}
