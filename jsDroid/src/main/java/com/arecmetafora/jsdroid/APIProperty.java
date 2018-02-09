package com.arecmetafora.jsdroid;

import com.arecmetafora.jsdroid.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * Class to map Java properties to JavaScript properties.
 */
class APIProperty extends APIParameter {

	/**
	 * Reflected field to invoke property get and set calls to native Java implementations.
	 */
	private Field reflectedProperty;

	/**
	 * Creates a new map between Java and JavaScript property.
	 *
	 * @param jsName JavaScript property name.
	 * @param f Reflected field to invoke property get and set calls to native Java implementations.
	 */
	APIProperty(String jsName, Field f) {
		super(jsName, f.getType(), f.getAnnotation(NonNull.class) == null);
		this.reflectedProperty = f;
	}

	/**
	 * @return the reflected field to invoke property get and set calls to native Java implementations.
	 */
	Field getReflectedProperty() {
		return this.reflectedProperty;
	}

}
