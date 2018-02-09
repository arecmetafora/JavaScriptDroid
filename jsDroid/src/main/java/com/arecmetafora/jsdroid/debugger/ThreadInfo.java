package com.arecmetafora.jsdroid.debugger;

/**
 * Debug thread representation.
 */
class ThreadInfo {

	/**
	 * Thread identifier.
	 */
	private int id;

	/**
	 * Name of the thread.
	 */
	private String name;

	/**
	 * Message related to the thread stop.
	 */
	private String message;

	/**
	 * Stack of execution calls until the thread stop.
	 */
	private StackFrame[] stackFrames;

	/**
	 * Creates a new debug thread.
	 *
	 * @param id Thread id (represented by the JavaScript pointer.
	 * @param message Thread identifier.
	 * @param stackFrames Stack of execution calls until the thread stop.
	 */
	ThreadInfo(int id, String message, StackFrame[] stackFrames) {
		this.id = id;
		this.message = message;
		this.stackFrames = stackFrames;
		this.name = Thread.currentThread().getName();
	}

	/**
	 * @return The thread identifier.
	 */
	int getId() {
		return this.id;
	}

	/**
	 * @return The message related to the thread stop.
	 */
	String getMessage() {
		return this.message;
	}

	/**
	 * @return The thread name.
	 */
	String getName() {
		return this.name;
	}

	/**
	 * @return The stack of execution calls until the thread stop.
	 */
	StackFrame[] getStackFrames() {
		return this.stackFrames;
	}
}
