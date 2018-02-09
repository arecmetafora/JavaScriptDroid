package com.arecmetafora.jsdroid;

import android.util.SparseArray;

import java.lang.reflect.InvocationTargetException;
import com.arecmetafora.jsdroid.debugger.Debugger;

/**
 * Class to receive all callbacks from JavaScript JNI API.
 */
final class JavaScriptCallback {

	/**
	 * Stores a hash containing the relation between constructor pointers and objects.
	 */
	private static SparseArray<AllocationInfo> allocationTable = new SparseArray<>();

	/**
	 * Private constructor.
	 */
	private JavaScriptCallback() {
	}

	/**
	 * Holds an object reference so that it wont't be finalized by the Java garbage collector before
	 * being used by the JavScriptCore.
	 *
	 * @param object The object which instance will be hold.
	 * @return the hash code of the object
	 */
	static int allocObjectReference(Object object) {
		synchronized (allocationTable) {

			int hashCode = object.hashCode();
			AllocationInfo allocInfo = allocationTable.get(hashCode);
			if (allocInfo != null) {
				allocInfo.allocReference();
				allocationTable.put(hashCode, allocInfo);
			} else {
				allocationTable.put(hashCode, new AllocationInfo(object));
			}
			return hashCode;

		}
	}

	/**
	 * Callback called when a construction of a object is evaluated in JavaScript.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 * @param className The name of the class that is being constructed.
	 * @param args The arguments of the constructor call.
	 * @return The new instance of the object that has been instantiated.
	 */
	static Object callbackConstructor(int jsContextPointer,
		String className, Object... args) throws Throwable {
		try {

			// Getting the mapped method representation
			APIClass apiClass = JavaScriptDroid.getAPIClassByName(className);

			if(apiClass.getReflectedConstructor() == null) {
				return null;
			}

			// Resolve the parameters, validating type casts
			Object[] params = resolveInvokeParameters(
				apiClass.getJSName(), "<constructor>",
				JavaScriptCallbackContext.CONSTRUCTOR,
				apiClass.getConstructorParameters(), args);

			// Calls the method execution and return the result
			return apiClass.getReflectedConstructor().newInstance(params);

		} catch (Exception ex) {
			handleException(jsContextPointer, ex);
		}

		return null;
	}

	/**
	 * Callback called when a function is evaluated in JavaScript.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 * @param obj The object which its function is being called.
	 * @param functionName The name of the function that is being called.
	 * @param args The arguments of the method call.
	 * @return The result of the function execution.
	 */
	static Object callbackFunction(int jsContextPointer, Object obj,
		String functionName, Object... args) throws Throwable {
		try {

			if (obj instanceof Debugger && "debug".equals(functionName)) {
				Debugger.getInstance().handleBreakpointHit(jsContextPointer);
			}

			// Getting the mapped method representation
			APIClass apiClass = JavaScriptDroid.getAPIClassByClass(obj.getClass());

			// Verifying if either the toString or valueOf method was called by
			// the JavaScript execution (explicitly or using concatenations to
			// other objects)
			if (functionName.equals("toString")
				|| functionName.equals("valueOf")
					|| functionName.equals("Symbol.toPrimitive")) {
				return String.format("[object %s]", apiClass.getJSName());
			}

			APIMethod apiMethod = apiClass.getMethod(functionName);

			// Resolve the parameters, validating type casts
			Object[] params = resolveInvokeParameters(
				apiClass.getJSName(), functionName,
				JavaScriptCallbackContext.METHOD,
				apiMethod.getParameters(), args);

			// Calls the method execution and return the result
			return apiMethod.getReflectedMethod().invoke(obj, params);

		} catch (Exception ex) {
			handleException(jsContextPointer, ex);
		}

		return null;
	}

	/**
	 * Callback called when an attempt to set a property for a JavaScript object is made.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 * @param obj The object which its property is being gotten.
	 * @param propertyName The name of the property that is being set.
	 * @return The value of the property that is being get.
	 */
	static Object callbackGetProperty(int jsContextPointer, Object obj,
		String propertyName) throws Throwable {
		try {
			// Getting the mapped property representation
			APIClass apiClass = JavaScriptDroid.getAPIClassByClass(obj.getClass());
			APIProperty apiProperty = apiClass.getProperty(propertyName);
			return apiProperty.getReflectedProperty().get(obj);

		} catch (Exception ex) {
			handleException(jsContextPointer, ex);
		}

		return null;
	}

	/**
	 * Callback called when an attempt to get a property for a JavaScript object is made.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 * @param obj The object which its property is being set.
	 * @param propertyName The name of the property that is being get.
	 * @param param The value of the property that is being set.
	 */
	static void callbackSetProperty(int jsContextPointer, Object obj,
		String propertyName, Object param) throws Throwable {
		try {

			// Getting the mapped property representation
			APIClass apiClass = JavaScriptDroid.getAPIClassByClass(
				obj.getClass());
			APIProperty apiProperty = apiClass.getProperty(propertyName);

			// Resolve the parameters, validating type casts
			Object[] params = resolveInvokeParameters(
					apiClass.getJSName(), propertyName,
                    JavaScriptCallbackContext.SETTER,
					new APIParameter[] {apiProperty}, param);

			apiProperty.getReflectedProperty().set(obj, params[0]);

		} catch (Exception ex) {
			handleException(jsContextPointer, ex);
		}
	}

	/**
	 * Releases an object reference.
	 *
	 * @param hashCode The hash code of the object which will be released.
	 */
	static void deallocObjectReference(int hashCode) {
		synchronized (allocationTable) {
			AllocationInfo allocInfo = allocationTable.get(hashCode);

			// The object reference has been already released.
			if (allocInfo == null) {
				return;
			}

			if (allocInfo.isUniqueReference()) {
				allocationTable.remove(hashCode);

				// Disposing the reference resources
				Object freedObject = allocInfo.getObjectReference();
				if (freedObject instanceof JavaScriptDisposable) {
					((JavaScriptDisposable) freedObject).dispose();
				}
			} else {
				allocInfo.deallocReference();
				allocationTable.put(hashCode, allocInfo);
			}
		}
	}

	/**
	 * Gets the pointer to the JavaScript class definition, in the JavaScript engine.
	 *
	 * @param obj The object which the class definition pointer will be obtained.
	 * @return The pointer to the JavaScript class of the given object.
	 */
	static int getJSClassRef(Object obj) throws JavaScriptClassUnregistered {
		APIClass apiClass = JavaScriptDroid.getAPIClassByClass(
			obj.getClass());
		if (apiClass != null) {
			return apiClass.getJSClassRefPointer();
		} else {
			throw new JavaScriptClassUnregistered(obj.getClass());
		}
	}

	/**
	 * Returns the reference of an object using its hash code.
	 *
	 * @param hashCode The hash code of the object
	 * @return Returns the object related with the hash code.
	 */
	static Object getObjectReference(int hashCode) {
		synchronized (allocationTable) {
			return allocationTable.get(hashCode).getObjectReference();
		}
	}


	/**
	 * Handle exceptions thrown during JavaScript evaluation, preparing the error context to be
	 * catch inside JavaScript code.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being
	 * executed.
	 * @param exceptionThrown The exception that was thrown.
	 */
	private static void handleException(int jsContextPointer, Exception exceptionThrown)
			throws Throwable {

		Throwable ex;
		if(exceptionThrown instanceof InvocationTargetException) {
			ex = exceptionThrown.getCause();
		} else {
			ex = exceptionThrown;
		}

		Debugger.getInstance().handleExceptionHit(jsContextPointer, ex);

		// Rethrows the exception to be catch by JavaScript code
		throw ex;
	}

	/**
	 * Checks if a JavaScript object has a property with a given name.
	 *
	 * @param obj The JavaScript object.
	 * @param propertyName The property name to be checked.
	 * @return Whether the JavaScript class has a method with this name.
	 */
	static boolean hasProperty(Object obj, String propertyName) {
		APIProperty p = JavaScriptDroid
				.getAPIClassByClass(obj.getClass()).getProperty(propertyName);
		if(p != null && p.getReflectedProperty() != null) {
			return true;
		} else {
			return hasMethod(obj, propertyName);
		}
	}

	/**
	 * Checks if a JavaScript object has a method with a given name.
	 *
	 * @param obj The JavaScript object.
	 * @param methodName The method name to be checked.
	 * @return Whether the JavaScript class has a method with this name.
	 */
	static boolean hasMethod(Object obj, String methodName) {
		APIMethod m = JavaScriptDroid
			.getAPIClassByClass(obj.getClass()).getMethod(methodName);
		return (m != null && m.getReflectedMethod() != null)
			|| methodName.equals("toString")
			|| methodName.equals("valueOf")
			|| methodName.equals("Symbol.toPrimitive");
	}

	/**
	 * Resolves the invocation parameters of a method, constructor or property set/get call,
	 * validating type casts when needed.
	 *
	 * @param className The name of the class which the callback is being called.
	 * @param propertyOrMethodName The name of the method/property that is being called in the
	 * callback.
	 * @param context The context of the JavaScript callback execution.
	 * @param parameters The type of the parameters that the invocation expects.
	 * @param parameterValues the parameters of the invocation call.
	 * @return The casted parameters, after conversions based on the types the invocation expects.
	 */
	private static Object[] resolveInvokeParameters(
		String className, String propertyOrMethodName, JavaScriptCallbackContext context,
		APIParameter[] parameters, Object... parameterValues)
            throws JavaScriptException {

		Object[] paramsToCall = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {

			// If parameter is omitted or explicit null
			if (i >= parameterValues.length || parameterValues[i] == null) {

				// parameter does not accept null values
				if (!parameters[i].isOptional()) {
					switch (context) {
						case SETTER:
							throw new JavaScriptPropertyRequired(className, propertyOrMethodName);
						case METHOD:
						case CONSTRUCTOR:
							throw new JavaScriptMethodParamRequired(className,
									propertyOrMethodName, parameters[i].getJSName());
						default:
					}
				}

			} else {

				Object paramValue = parameterValues[i];
				Class<?> paramType = parameters[i].getType();

				// Numbers must be converted, since the JavaScript returns always Double
				if (paramValue instanceof Number) {
					if (paramType.isAssignableFrom(double.class)
						|| paramType.isAssignableFrom(Double.class)) {
						paramsToCall[i] = ((Number) paramValue).doubleValue();
					} else if (paramType.isAssignableFrom(int.class)
						|| paramType.isAssignableFrom(Integer.class)) {
						paramsToCall[i] = ((Number) paramValue).intValue();
					} else if (paramType.isAssignableFrom(float.class)
						|| paramType.isAssignableFrom(Float.class)) {
						paramsToCall[i] = ((Number) paramValue).floatValue();
					} else if (paramType.isAssignableFrom(byte.class)
						|| paramType.isAssignableFrom(Byte.class)) {
						paramsToCall[i] = ((Number) paramValue).byteValue();
					} else if (paramType.isAssignableFrom(short.class)
						|| paramType.isAssignableFrom(Short.class)) {
						paramsToCall[i] = ((Number) paramValue).shortValue();
					} else if (paramType.isAssignableFrom(long.class)
						|| paramType.isAssignableFrom(Long.class)) {
						paramsToCall[i] = ((Number) paramValue).longValue();
					}

					// Boleans must be converted too, since the JavaScript
					// returns always Boolean objects (convert to primitive).
				} else if (paramValue instanceof Boolean) {
					if (paramType.isAssignableFrom(boolean.class)
						|| paramType.isAssignableFrom(Boolean.class)) {
						paramsToCall[i] = ((Boolean) paramValue).booleanValue();
					}
				}

				// If the parameter was not set by number or booleans
				// (primitives or not), validates the types against the expected
				// types from invocation call.
				if (paramsToCall[i] == null) {
					switch (context) {
						case SETTER:
							if (paramValue != null && paramType != null
									&& !paramType.isAssignableFrom(paramValue.getClass())) {
								throw new JavaScriptPropertyTypeInvalid(className, parameters[i]);
							}
							break;
						case METHOD:
							validateMethodParameter(
								className, propertyOrMethodName, parameters[i], paramValue);
							break;
						case CONSTRUCTOR:
							validateMethodParameter(className, "<constructor>",
									parameters[i], paramValue);
							break;
						default:
					}

					// If no validation error was thrown, the parameter is
					// valid. So set it and continue.
					paramsToCall[i] = paramValue;
				}
			}
		}

		return paramsToCall;
	}

	/**
	 * Checks if a value is valid as a parameter in a method.
	 *
	 * @param jsName The JavaScript entity that is being validated.
	 * @param methodName The method name.
	 * @param param The method parameter.
	 * @param value value being passed as a parameter.
	 */
	private static void validateMethodParameter(String jsName,
											   String methodName, APIParameter param,
											   Object value) throws JavaScriptException {

		if (value != null && param.getType().isArray()) {

			// Check if received value is array
			if (!value.getClass().isArray()) {
				throw new JavaScriptMethodParamTypeInvalid(jsName, methodName, param);
			}

			// Cast received value to a object array
			Object[] valueArray = (Object[]) value;

			// Returns a Class object which represents the component
			// type if this class represents an array type.
			Class<?> paramElementType = param.getType().getComponentType();

			// Check if all elements of the array are of the required type
			for (Object objElement : valueArray) {
				if (objElement != null
						&& !paramElementType.isAssignableFrom(objElement
						.getClass())) {
					throw new JavaScriptMethodParamTypeInvalid(jsName, methodName, param);
				}

			}
		} else if (value != null && !param.getType().isAssignableFrom(value.getClass())) {
			throw new JavaScriptMethodParamTypeInvalid(jsName, methodName, param);
		}
	}

	/**
	 * Callback called when a JavaScript exception has been identified and a native Java exception
	 * must be thrown to the application.
	 *
	 * @param jsContextPointer Pointer to the JavaScript context which this callback is being executed.
	 * @param message The exception message.
	 * @param stacktrace The current stack trace of the script execution, at the moment of the
	 * exception occurrence.
	 */
	static void throwException(int jsContextPointer, String message, String stacktrace)
			throws JavaScriptException {

		JavaScriptException ex = new JavaScriptException(message, stacktrace);
		Debugger.getInstance().handleExceptionHit(jsContextPointer, ex);
		throw ex;
	}
}
