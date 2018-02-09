#include "JSCCallback.h"

_JSCCallback JSCCallback;
extern JavaVM *gJavaVM;

/**
 * Release all native Java references created inside the JNI layer and stored in a array.
 *
 * @param env Pointer to JNI environment.
 * @param javaArray The Java native Array to be released.
 */
void DeleteLocalRefsFromArray(JNIEnv *env, jobjectArray* javaArray)
{
	// Don't free items if an exception has been thrown
	if(env->ExceptionOccurred()) return;

	// If the parameter is an array, deletes the local reference of each one of its items
	int length = env->GetArrayLength(*javaArray);
	for(int i=0; i < length; i++)
	{
		jobject javaArrayItem = env->GetObjectArrayElement(*javaArray, i);
		if(javaArrayItem)
		{
			env->DeleteLocalRef(javaArrayItem);
		}
	}
	env->DeleteLocalRef(*javaArray);
}

/**
 * The callback invoked when getting a property's value.
 * If this function returns NULL, the get request forwards to object's statically declared properties, then its parent class chain (which includes the default object class), then its prototype chain.
 *
 * @param ctx The execution context to use.
 * @param object The JSObject to search for the property.
 * @param propertyName A JSString containing the name of the property to get.
 * @param exception A pointer to a JSValueRef in which to return an exception, if any.
 *
 * @return The property's value if object has the property, otherwise NULL.
 */
JSValueRef callbackGetProperty(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef* exception)
{
	// Bypassing the callback of the "isMappedObject" property, indicating that this object is instance of a mapped object
	if(JSStringIsEqualToUTF8CString(propertyName, "isMappedObject")) return JSValueMakeBoolean(ctx, true);

	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();
    bool vmAttached = false;
    if(!env) {
        gJavaVM->AttachCurrentThread(&env, NULL);
        vmAttached = true;
    }

	// Object which the callback is being called
    jstring propName = JSStringToJavaString(env, propertyName);
	jobject obj = GetObjectReference(env, object);

	// Checks if the callback was called by a function evaluation. If so, pass the callback for a function call
	if(HasMethod(env, obj, propName))
	{
		// Creates a JavaScript function and sets its callback to be executed later
		JSValueRef result = JSObjectMakeFunctionWithCallback(ctx, propertyName, (JSObjectCallAsFunctionCallback) callbackFunction);

		// Saves the method name in a property of the created JavaScript function to be accessed later
		JSStringRef selectorName = JSStringCreateWithUTF8CString("selector");
		JSObjectSetProperty(ctx, (JSObjectRef)result, selectorName, JSValueMakeString(ctx, propertyName), kJSPropertyAttributeReadOnly, exception);
		JSStringRelease(selectorName);

		// Releasing local references
		env->DeleteLocalRef(obj);
		env->DeleteLocalRef(propName);

		return result;
	}

	// Calling the callback execution
	jobject result = env->CallStaticObjectMethod(JSCCallback.handler, JSCCallback.callbackGetPropertyMethodID, ctx, obj, propName);

	// Releasing local references
	env->DeleteLocalRef(obj);
	env->DeleteLocalRef(propName);

	JSValueRef jsReturn = NULL;

	// Handling exception thrown during callback execution
	if(!HandleJavaException(env, ctx, exception)) {
		// If no error was thrown during the callback execution, convert and return the result as a JavaScript instance
		jsReturn = JSValueMakeWithNativeInstance(env, ctx, result);
		env->DeleteLocalRef(result);
	}

    if(vmAttached) {
        gJavaVM->DetachCurrentThread();
    }

	return jsReturn;
}

/**
 * The callback invoked when setting a property's value.
 * If this function returns false, the set request forwards to object's statically declared properties, then its parent class chain (which includes the default object class).
 *
 * @param ctx The execution context to use.
 * @param object The JSObject on which to set the property's value.
 * @param propertyName A JSString containing the name of the property to set.
 * @param value A JSValue to use as the property's value.
 * @param exception A pointer to a JSValueRef in which to return an exception, if any.
 *
 * @return true if the property was set, otherwise false.
 */
bool callbackSetProperty(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef value, JSValueRef* exception)
{
	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();

    if(!callbackHasProperty(ctx, object, propertyName)) {
        return false;
    }

	// Object which the callback is being called
	jobject obj = GetObjectReference(env, object);
	jstring propName = JSStringToJavaString(env, propertyName);
	jobject param = JSObjectToNative(env, ctx, value);

	// Calling the callback execution
	env->CallStaticVoidMethod(JSCCallback.handler, JSCCallback.callbackSetPropertyMethodID, ctx, obj, propName, param);

	// Releasing local references
	env->DeleteLocalRef(obj);
	env->DeleteLocalRef(propName);
	env->DeleteLocalRef(param);

	// Handling exception thrown during callback execution
	HandleJavaException(env, ctx, exception);

	return true;
}

/**
 * The callback invoked when an object is called as a function.
 * If your callback were invoked by the JavaScript expression 'myObject.myFunction()', function would be set to myFunction, and thisObject would be set to myObject.
 * If this callback is NULL, calling your object as a function will throw an exception.
 *
 * @param ctx The execution context to use.
 * @param function A JSObject that is the function being called.
 * @param thisObject A JSObject that is the 'this' variable in the function's scope.
 * @param argumentCount An integer count of the number of arguments in arguments.
 * @param arguments A JSValue array of the  arguments passed to the function.
 * @param exception A pointer to a JSValueRef in which to return an exception, if any.
 *
 * @return A JSValue that is the function's return value.
 */
JSValueRef callbackFunction(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount,
							const JSValueRef arguments[], JSValueRef *exception)
{
	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();

	// Object which the callback is being called
	jobject obj = GetObjectReference(env, thisObject);

	// Obtaining the function name
	JSStringRef selectorName = JSStringCreateWithUTF8CString("selector");
    JSStringRef jsSelector = JSValueToStringCopy(ctx, JSObjectGetProperty(ctx, function, selectorName, 0), 0);
	JSStringRelease(selectorName);
    jstring functionName = JSStringToJavaString(env, jsSelector);
	JSStringRelease(jsSelector);

	// Creating the constructor parameters
	jobjectArray params = NewJavaArrayFromJSArguments(env, ctx, arguments, argumentCount);

	// Calling the callback execution
	jobject result = env->CallStaticObjectMethod(JSCCallback.handler, JSCCallback.callbackFunctionMethodID, ctx, obj, functionName, params);

	// Releasing local references
	env->DeleteLocalRef(obj);
	env->DeleteLocalRef(functionName);

	JSValueRef jsReturn = NULL;

	// Handling exception thrown during callback execution
	if(!HandleJavaException(env, ctx, exception)) {
		// If no error was thrown during the callback execution, convert and return the result as a JavaScript instance
		jsReturn = JSValueMakeWithNativeInstance(env, ctx, result);
		env->DeleteLocalRef(result);
	}

	DeleteLocalRefsFromArray(env, &params);

	return jsReturn;
}


/**
 * The callback invoked when an object is used as a constructor in a 'new' expression.
 * If your callback were invoked by the JavaScript expression 'new myConstructor()', constructor would be set to myConstructor.
 * If this callback is NULL, using your object as a constructor in a 'new' expression will throw an exception.
 *
 * @param ctx The execution context to use.
 * @param constructor A JSObject that is the constructor being called.
 * @param argumentCount An integer count of the number of arguments in arguments.
 * @param arguments A JSValue array of the  arguments passed to the function.
 * @param exception A pointer to a JSValueRef in which to return an exception, if any.
 *
 * @return A JSObject that is the constructor's return value.
 */
JSObjectRef callbackConstructor(JSContextRef ctx, JSObjectRef constructor, size_t argumentCount, 
								const JSValueRef arguments[], JSValueRef* exception)
{
	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();

	// Getting the name of the JavaScript class
	JSStringRef jsClassNameProperty = JSStringCreateWithUTF8CString("name");
    JSStringRef jsClassName = JSValueToStringCopy(ctx, JSObjectGetProperty(ctx, constructor, jsClassNameProperty, 0), 0);
	JSStringRelease(jsClassNameProperty);

	// Obtaining the class name
    jstring className = JSStringToJavaString(env, jsClassName);
	JSStringRelease(jsClassName);

	// Creating the constructor parameters
	jobjectArray params = NewJavaArrayFromJSArguments(env, ctx, arguments, argumentCount);

	// Calling the callback execution
	jobject result = env->CallStaticObjectMethod(JSCCallback.handler, JSCCallback.callbackConstructorMethodID, ctx, className, params);

	// Releasing local references
	env->DeleteLocalRef(className);

	JSObjectRef jsReturn = NULL;

	// Handling exception thrown during callback execution
	if(!HandleJavaException(env, ctx, exception)) {
		// If no error was thrown during the callback execution, convert and return the result as a JavaScript instance
        if(result) {
            jsReturn = JSObjectMakeWithNativeInstance(env, ctx, result);
            env->DeleteLocalRef(result);
        }
	}

	DeleteLocalRefsFromArray(env, &params);

	return jsReturn;
}

/**
 * The callback invoked when an object is finalized (prepared for garbage collection). An object may be finalized on any thread.
 *
 * @param object object The JSObject being finalized.
 */
void callbackFinalize(JSObjectRef object)
{
	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();

	// Releases all references to this object
	DeallocObjectReference(env, object);
}

/**
 * The callback invoked when determining whether an object has a property.
 * If this function returns false, the hasProperty request forwards to object's statically declared properties, then its parent class chain (which includes the default object class), then its prototype chain.
 * This callback enables optimization in cases where only a property's existence needs to be known, not its value, and computing its value would be expensive.
 * If this callback is NULL, the getProperty callback will be used to service hasProperty requests.
 *
 *
 * @param ctx The execution context to use.
 * @param object The JSObject to search for the property.
 * @param propertyName A JSString containing the name of the property look up.
 *
 * @return true if object has the property, otherwise false.
 */
bool callbackHasProperty(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName)
{
	// Gets the JNIEnv variable.
	JNIEnv *env = GetEnvironment();

	jstring propName = JSStringToJavaString(env, propertyName);
	jobject obj = GetObjectReference(env, object);

	jboolean hasProperty = env->CallStaticBooleanMethod(JSCCallback.handler, JSCCallback.hasPropertyMethodID, obj, propName);

	env->DeleteLocalRef(obj);
	env->DeleteLocalRef(propName);

	return hasProperty;
}

/*
 * Allocate a object reference, returning its identifier to bind with the JavaScript object instance.
 *
 * @param env Pointer to JNI environment.
 * @param obj Native Java object to be allocated.
 *
 * @returns A pointer to the native Java object instance that was allocated.
 */
long* AllocObjectReference(JNIEnv *env, jobject obj)
{
	jint objHashCode = env->CallStaticIntMethod(JSCCallback.handler, JSCCallback.allocObjectReferenceMethodID, obj);

	long* objHashCodePointer = (long*)malloc(sizeof(long));
	objHashCodePointer[0] = objHashCode;

	return objHashCodePointer;
}

/*
 * Deallocate a object reference so it can be collected by the native Java garbage collector.
 *
 * @param env Pointer to JNI environment.
 * @param jsObject JavaScript object that was collected.
 */
void DeallocObjectReference(JNIEnv *env, JSObjectRef jsObject)
{
	long* objHashCode = (long*)JSObjectGetPrivate(jsObject);

	env->CallStaticVoidMethod(JSCCallback.handler, JSCCallback.deallocObjectReferenceMethodID, (jint) objHashCode[0]);

	free(objHashCode);
}

/*
 * Gets the instance of a allocated object, given its identifier, so it can be accessed by native Java methods.
 *
 * @param env Pointer to JNI environment.
 * @param jsObject JavaScript object that holds the nativa Java object instance.
 *
 * @returns The Java native instance.
 */
jobject GetObjectReference(JNIEnv *env, JSObjectRef jsObject)
{
	long* objHashCode = (long*)JSObjectGetPrivate(jsObject);

	return env->CallStaticObjectMethod(JSCCallback.handler, JSCCallback.getObjectReferenceMethodID, objHashCode[0]);
}

/*
 * Checks if the mapped JavaScript object has a mapped method with the given name.
 *
 * @param env Pointer to JNI environment.
 * @param methodName Name of the method to be checked.
 *
 * @returns true if there is a method mapped to the given object, of false otherwise.
 */
bool HasMethod(JNIEnv *env, jobject obj, jstring methodName)
{
	return env->CallStaticBooleanMethod(JSCCallback.handler, JSCCallback.hasMethodMethodID, obj, methodName);
}
