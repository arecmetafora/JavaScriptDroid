package com.arecmetafora.jsdroid.debugger;

/**
 * Class to receive all callbacks from JavaScript JNI Debugger.
 */
final class DebuggerCallback {

    /**
     * Private constructor.
     */
    private DebuggerCallback() {
    }

    /**
     * Callback called when a JavaScript source is parsed.
     *
     * @param jsContextPointer Pointer to the JavaScript context which this callback is being
     * @param sourceUrl The JavaScript file name.
     * @param sourceID The JavaScript source identifier.
     */
    static void sourceParsed(int jsContextPointer, String sourceUrl, int sourceID) {
        Debugger.getInstance().sourceParsed(jsContextPointer, sourceUrl, sourceID);
    }

    /**
     * Callback called when a breakpoint is hit.
     *
     * @param jsContextPointer Pointer to the JavaScript context which this callback is being
     * @param sourceID The JavaScript source identifier.
     * @param line The current line of the JavaScript source.
     * @param column The current column of the JavaScript source.
     */
    static void handleBreakpointHit(int jsContextPointer, int sourceID, int line, int column) {
        Debugger.getInstance().handleBreakpointHit(jsContextPointer);
    }

    /**
     * Callback called when an exception is hit.
     *
     * @param jsContextPointer Pointer to the JavaScript context which this callback is being
     * @param sourceID The JavaScript source identifier.
     * @param line The current line of the JavaScript source.
     * @param column The current column of the JavaScript source.
     * @param exception The exception thrown.
     */
    static void handleExceptionHit(int jsContextPointer, int sourceID, int line, int column,
                                          Throwable exception) {
        Debugger.getInstance().handleExceptionHit(jsContextPointer, exception);
    }

    /**
     * Callback called when a breakpoint step is hit.
     *
     * @param jsContextPointer Pointer to the JavaScript context which this callback is being
     * @param sourceID The JavaScript source identifier.
     * @param line The current line of the JavaScript source.
     * @param column The current column of the JavaScript source.
     */
    static void handleStepHit(int jsContextPointer, int sourceID, int line, int column) {
        Debugger.getInstance().handleBreakpointHit(jsContextPointer);
    }
}
