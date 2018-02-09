package com.arecmetafora.jsdroid.debugger;

/**
 * Abstraction of a thread related debug message.
 */
abstract class ThreadMessage {

    /**
     * Thread in which the evaluation should be executed.
     */
    private ThreadInfo threadInfo;

    /**
     * Creates a thread debug message.
     *
     * @param threadInfo Thread in which the message should be handled.
     */
    ThreadMessage(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    /**
     * @return The thread in which the evaluation should be executed.
     */
    ThreadInfo getThreadInfo() {
        return this.threadInfo;
    }
}
