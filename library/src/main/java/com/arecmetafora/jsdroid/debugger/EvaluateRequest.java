package com.arecmetafora.jsdroid.debugger;

/**
 * Debug evaluate request.
 */
class EvaluateRequest extends ThreadMessage {

	/**
	 * Evaluation expression.
	 */
	private String expression;

	/**
	 * Creates a debug evaluate request.
	 *
	 * @param expression Evaluation expression.
	 * @param threadInfo Thread in which the evaluation should be executed.
	 */
	EvaluateRequest(String expression, ThreadInfo threadInfo) {
		super(threadInfo);
		this.expression = expression;
	}

	/**
	 * @return The evaluation expression.
	 */
	String getExpression() {
		return this.expression;
	}
}
