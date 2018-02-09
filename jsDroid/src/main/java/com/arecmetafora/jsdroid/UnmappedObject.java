package com.arecmetafora.jsdroid;

/**
 * This class is used to instantiate objects which is not mapped, but need to be passed as
 * argument in a callback.
 *
 * For example, when executing the code <code>new Numeric().value = new MyBllObj()</code>,
 * the {@link JavaScriptCallback#callbackSetProperty(int, Object, String, Object)} method
 * must be called passing a valid instance so the callback can perform
 * the type validations and thrown the invalid type error.
 */
public final class UnmappedObject {

}