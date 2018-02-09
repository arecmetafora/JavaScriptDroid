package com.arecmetafora.jsdroid;

/**
 * Exception thrown when a property is being set with null value, but it does not accept null.
 */
public final class JavaScriptPropertyRequired extends JavaScriptException {

    /**
     * Creates a new exception.
     *
     * @param jsClass The name of the JavaScript class which owns the property.
     * @param jsProperty The property name.
     */
    JavaScriptPropertyRequired(String jsClass, String jsProperty) {
        super(String.format(
                "Property '%s' of '%s' does not accept null", jsProperty, jsClass));
    }
}
