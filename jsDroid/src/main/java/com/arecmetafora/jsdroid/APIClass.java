package com.arecmetafora.jsdroid;

import com.arecmetafora.jsdroid.annotation.JavaScriptMapped;
import com.arecmetafora.jsdroid.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Class to map Java classes to JavaScript classes.
 */
class APIClass {

	/**
	 * The name of the JavaScript class.
	 */
	private String jsName;

	/**
	 * A pointer to the JavaScript class definition (JSClassRef).
	 */
	private int jsClassRefPointer = 0;

	/**
	 * JsName X APIProperty.
	 */
	private Hashtable<String, APIProperty> propertiesByName;

	/**
	 * JsName X APIMethod.
	 */
	private Hashtable<String, APIMethod> methodsByName;

	/**
	 * Reflected method to invoke construction calls to native Java implementations.
	 */
	private Constructor<?> reflectedConstructor;

	/**
	 * Collection with all constructor parameters.
	 */
	private APIParameter[] constructorParameters;

	/**
	 * Private constructor to centralize all logic to create a new map between Java and JavaScript
	 * classes.
	 *
	 * @param clazz The unique instance of Class that represents this object's class.
	 */
	APIClass(Class<?> clazz) {
		this.propertiesByName = new Hashtable<>();
		this.methodsByName = new Hashtable<>();

		JavaScriptMapped jsClassMapping = clazz.getAnnotation(JavaScriptMapped.class);
		if(jsClassMapping != null) {
			this.jsName = jsClassMapping.name();
		} else {
			this.jsName = clazz.getSimpleName();
		}

		for (Constructor<?> c : clazz.getConstructors()) {
			if (c.getAnnotation(JavaScriptMapped.class) != null) {
				if (this.reflectedConstructor == null) {
					this.reflectedConstructor = c;
					break;
				}
			}
		}
		if(this.reflectedConstructor == null) {
			this.reflectedConstructor = clazz.getDeclaredConstructors()[0];
		}

		this.constructorParameters =
				new APIParameter[this.reflectedConstructor.getParameterTypes().length];
		for(int i=0; i < this.constructorParameters.length; i++) {
			boolean isOptional = true;
			for(Annotation paramAnnotation : this.reflectedConstructor.getParameterAnnotations()[i]) {
				if(paramAnnotation.annotationType().equals(NonNull.class)) {
					isOptional = false;
					break;
				}
			}
			APIParameter param = new APIParameter("arg" + i,
					this.reflectedConstructor.getParameterTypes()[i], isOptional);
			this.constructorParameters[i] = param;
		}

		for (Method m : clazz.getMethods()) {
			JavaScriptMapped jsMethodMapping = m.getAnnotation(JavaScriptMapped.class);
			if (jsMethodMapping != null && !m.isSynthetic()) {
				String jsMethodName;
				if(!Utils.isNullOrEmpty(jsMethodMapping.name())) {
					jsMethodName = jsMethodMapping.name();
				} else {
					jsMethodName = m.getName();
				}
				APIMethod jsMethod = new APIMethod(jsMethodName, m);
				methodsByName.put(jsMethodName, jsMethod);
			}
		}

		for (Field p : clazz.getFields()) {
			JavaScriptMapped jsPropertyMapping = p.getAnnotation(JavaScriptMapped.class);
			if (jsPropertyMapping != null) {
				String jsPropertyName;
				if(!Utils.isNullOrEmpty(jsPropertyMapping.name())) {
					jsPropertyName = jsPropertyMapping.name();
				} else {
					jsPropertyName = p.getName();
				}
				APIProperty jsProperty = new APIProperty(jsPropertyName, p);
				propertiesByName.put(jsPropertyName, jsProperty);
			}
		}
	}

	/**
	 * @return Collection with all constructor parameters.
	 */
	APIParameter[] getConstructorParameters() {
		return this.constructorParameters;
	}

	/**
	 * Gets a pointer to the JavaScript class definition (JSClassRef).
	 *
	 * @return A pointer to the JavaScript class definition (JSClassRef).
	 */
	int getJSClassRefPointer() {
		return this.jsClassRefPointer;
	}

	/**
	 * Sets a pointer to the JavaScript class definition (JSClassRef).
	 *
	 * @param jsClassRefPointer A pointer to the JavaScript class definition (JSClassRef).
	 */
	void setJSClassRefPointer(int jsClassRefPointer) {
		this.jsClassRefPointer = jsClassRefPointer;
	}

	/**
	 * Gets the name of the JavaScript class.
	 *
	 * @return The name of the JavaScript class.
	 */
	String getJSName() {
		return this.jsName;
	}

	/**
	 * Gets the specified method information.
	 *
	 * @param jsName Javascript method name.
	 * @return Method information.
	 */
	APIMethod getMethod(String jsName) {
		return this.methodsByName.get(jsName);
	}

	/**
	 * @return All properties mapped for this class.
	 */
	Enumeration<APIProperty> getProperties() {
		return this.propertiesByName.elements();
	}

	/**
	 * Gets the specified property information.
	 *
	 * @param jsName Javascript property name.
	 * @return Method information.
	 */
	APIProperty getProperty(String jsName) {
		return this.propertiesByName.get(jsName);
	}

	/**
	 * @return the reflected method to invoke construction calls to native Java implementations.
	 */
	Constructor<?> getReflectedConstructor() {
		return this.reflectedConstructor;
	}
}
