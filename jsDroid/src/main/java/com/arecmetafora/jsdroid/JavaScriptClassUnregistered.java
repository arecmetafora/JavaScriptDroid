package com.arecmetafora.jsdroid;

/**
 * Exception thrown when the JavaScript interpreter tried to convert a class that was not registered.
 */
public final class JavaScriptClassUnregistered extends JavaScriptException {

    /**
     * Creates a new exception.
     *
     * @param clazz The type of the class that was not registered.
     */
    JavaScriptClassUnregistered(Class<?> clazz) {
        super(String.format("Class %s not registered. Did you forget to call registerClass first?",
                clazz.getSimpleName()));
    }
}
