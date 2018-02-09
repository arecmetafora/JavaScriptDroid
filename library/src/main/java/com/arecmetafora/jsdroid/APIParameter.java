package com.arecmetafora.jsdroid;

/**
 * Class to map JavaScript parameters.
 */
class APIParameter {

	/**
	 * The name of the JavaScript parameter.
	 */
	private String jsName;

	/**
	 * Parameter type.
	 */
	private Class<?> type;

	/**
	 * Whether the parameter is optional.
	 */
	private boolean isOptional;

	/**
	 * Constructor of the APIParameter.
	 *
	 * @param jsName Name of the parameter.
	 * @param type Parameter type.
	 * @param isOptional Boolean to indicate if the parameter is optional.
	 */
	APIParameter(String jsName, Class<?> type, boolean isOptional) {
		this.jsName = jsName;
		this.type = type;
		this.isOptional = isOptional && !type.isPrimitive();
	}

	/**
	 * @return the name of the JavaScript parameter.
	 */
	String getJSName() {
		return this.jsName;
	}

	/**
	 * @return Whether the parameter is optional.
	 */
	boolean isOptional() {
		return this.isOptional;
	}

	/**
	 * @return Parameter type.
	 */
	Class<?> getType() {
		return type;
	}
}
