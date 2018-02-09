package com.arecmetafora.jsdroid;

import com.arecmetafora.jsdroid.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Class to map Java methods to JavaScript methods.
 */
class APIMethod {

	/**
	 * The name of the JavaScript method.
	 */
	private String jsName;

	/**
	 * Reflected method to invoke method calls to native Java implementations.
	 */
	private Method reflectedMethod;

	/**
	 * List of all method parameters.
	 */
	private APIParameter[] parameters;

	/**
	 * Creates a new map between Java and JavaScript method.
	 *
	 * @param jsName JavaScript method name.
	 * @param m reflected method
	 */
	APIMethod(String jsName, Method m) {
		this.jsName = jsName;
		this.parameters = new APIParameter[m.getParameterTypes().length];
		this.reflectedMethod = m;

		for(int i=0; i < this.parameters.length; i++) {
			boolean isOptional = true;
			for(Annotation paramAnnotation : m.getParameterAnnotations()[i]) {
				if(paramAnnotation.annotationType().equals(NonNull.class)) {
					isOptional = false;
					break;
				}
			}
			APIParameter param = new APIParameter("arg" + i, m.getParameterTypes()[i], isOptional);
			this.parameters[i] = param;
		}
	}

	/**
	 * Gets the JavaScript method name.
	 *
	 * @return JavaScript class name.
	 */
	String getJSName() {
		return this.jsName;
	}

	/**
	 * @return The parameters of this method.
	 */
	APIParameter[] getParameters() {
		return this.parameters;
	}

	/**
	 * @return the reflected method to invoke method calls to native Java implementations.
	 */
	Method getReflectedMethod() {
		return this.reflectedMethod;
	}
}
