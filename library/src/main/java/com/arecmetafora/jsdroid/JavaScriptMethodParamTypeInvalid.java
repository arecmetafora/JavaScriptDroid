package com.arecmetafora.jsdroid;

/**
 * Exception thrown when a property is being set with a type different than expected.
 */
public final class JavaScriptMethodParamTypeInvalid extends JavaScriptException {

    /**
     * Creates a new exception.
     *
     * @param jsClass The name of the JavaScript class which owns the property.
     * @param jsMethod The method name.
     * @param param The method parameter information.
     */
    JavaScriptMethodParamTypeInvalid(String jsClass, String jsMethod, APIParameter param) {
        super(String.format(
                "Parameter '%s' of method '%s' from '%s' is not instance of '%s'", param.getJSName(),
                jsMethod, jsClass, param.getType().getSimpleName()));
    }
}
