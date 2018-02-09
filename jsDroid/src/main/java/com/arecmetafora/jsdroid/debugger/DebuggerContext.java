package com.arecmetafora.jsdroid.debugger;

import com.arecmetafora.jsdroid.Utils;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents a JavaScript context during Debugger execution.
 */
class DebuggerContext {

    /**
     * Stores a hash containing the relation between source names and ids.
     */
    private HashMap<String, Integer> sources = new HashMap<>();

    /**
     * List of breakpoints
     */
    private LinkedList<Breakpoint> breakpoints = new LinkedList<>();

    /**
     * List of messages to be processed while the context is waiting for command.
     */
    private LinkedList<ProtocolMessage> debuggerMessages = new LinkedList<>();

    /**
     * Gets the id of a JavaScript source.
     *
     * @param fileName The JavaScript source name.
     * @return The source identifier.
     */
    int getSourceIdByFileName(String fileName) {
        return Utils.defaultIfNull(sources.get(fileName), -1);
    }

    /**
     * @return The list of breakpoint hits.
     */
    LinkedList<Breakpoint> getBreakpoints() {
        return breakpoints;
    }

    /**
     * @return The hash containing the relation between source names and ids.
     */
    HashMap<String, Integer> getSources() {
        return sources;
    }

    /**
     * Appends a message to be executed in the context of this thread.
     * @param message A message to be sent
     */
    void putMessage(ProtocolMessage message) {
        debuggerMessages.add(message);
    }

    /**
     * @return The next debugger message to be processed by this thread.
     */
    ProtocolMessage nextDebuggerMessage() {
        if(debuggerMessages.isEmpty()) {
            return null;
        } else {
            return debuggerMessages.removeFirst();
        }
    }
}
