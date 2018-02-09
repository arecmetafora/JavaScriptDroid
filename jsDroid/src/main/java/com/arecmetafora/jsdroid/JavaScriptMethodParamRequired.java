package com.arecmetafora.jsdroid;

/**
 * Exception thrown when a method parameter is being set with null value, but it does not accept null.
 */
public final class JavaScriptMethodParamRequired extends JavaScriptException {

    /**
     * Creates a new exception.
     *
     * @param jsClass The name of the JavaScript class which owns the property.
     * @param jsMethod The method name.
     * @param jsMethodParam The method parameter information.
     */
    JavaScriptMethodParamRequired(String jsClass, String jsMethod, String jsMethodParam) {
        super(String.format(
                "Parameter '%s' of method '%s' from '%s' does not accept null",
                jsMethodParam, jsMethod, jsClass));
    }
}
