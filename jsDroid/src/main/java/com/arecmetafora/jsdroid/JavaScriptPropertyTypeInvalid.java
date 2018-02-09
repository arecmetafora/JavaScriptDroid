package com.arecmetafora.jsdroid;

/**
 * Exception thrown when a property is being set with a type different than expected.
 */
public final class JavaScriptPropertyTypeInvalid extends JavaScriptException {

    /**
     * Creates a new exception.
     *
     * @param jsClass The name of the JavaScript class which owns the property.
     * @param property The property information.
     */
    JavaScriptPropertyTypeInvalid(String jsClass, APIParameter property) {
        super(String.format(
                "Property '%s' of '%s' is not instance of '%s'", property.getJSName(), jsClass,
                property.getType().getSimpleName()));
    }
}
