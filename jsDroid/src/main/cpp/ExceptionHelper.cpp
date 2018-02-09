#include "ExceptionHelper.h"
#include "ConversionHelper.h"

/*
 * Creates a JavaScript error within a native Java Exception that has been thrown during a callback execution..
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param javaException Native Java exception thrown.
 *
 * @returns A JavaScript error representing the exception that was thrown.
 */
JSValueRef NewJsErrorFromJavaException(JNIEnv *env, JSContextRef ctx, jthrowable javaException)
{
	// Obtaining the exception message
	jmethodID getMessageMethod = env->GetMethodID(JavaClasses.Exception, "getMessage", "()Ljava/lang/String;");
	jstring messageResult = (jstring)env->CallObjectMethod(javaException, getMessageMethod);

    const char *resultString = env->GetStringUTFChars(messageResult, 0);
    JSStringRef errorMessage = JSStringCreateWithUTF8CString(resultString);
    JSValueRef jsErrorMessage = JSValueMakeString(ctx, errorMessage);
    JSStringRelease(errorMessage);
    env->ReleaseStringUTFChars(messageResult, resultString);
    env->DeleteLocalRef(messageResult);

	JSObjectRef errorObject = JSObjectMakeError(ctx, 1, &jsErrorMessage, NULL);

	// Saves the exception object as an internal property to be accessed later
	// (if the exception was not catch and raised by JavaScript)
	intptr_t errPtr = (intptr_t)javaException;
	JSStringRef internalErrorName = JSStringCreateWithUTF8CString("internalError");
	JSValueRef internalError = JSValueMakeNumber(ctx, errPtr);
	JSObjectSetProperty(ctx, errorObject, internalErrorName, internalError, 0, NULL);
	JSStringRelease(internalErrorName);

	return errorObject;
}

/**
 * Check and handle exceptions thrown by Java, if it has occurred.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param exception A pointer to the JavaScript exception, to return.
 *
 * @return true whether an exception has been handled or false otherwise.
 * In case of affirmative, you must immediately abort the current scope execution.
 */
bool HandleJavaException(JNIEnv* env, JSContextRef ctx, JSValueRef* exception)
{
	if(env->ExceptionCheck())
	{
		jthrowable exc = env->ExceptionOccurred();
		env->ExceptionClear();

		// Creating a JavaScript exception with the thrown native Java exception
		*exception = NewJsErrorFromJavaException(env, ctx, exc);

		// In case the exception could haven't been converted, throw the exception itself
		if(!*exception) {
			env->Throw(exc);
		}

		return true;
	}

	return false;
}

/**
 * Check and handle exceptions thrown by JavaScript, if it has occurred.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param exception The JavaScript exception thrown during evaluation.
 *
 * @return true whether an exception has been handled or false otherwise.
 * In case of affirmative, you must immediately abort the current scope execution.
 */
bool HandleJSException(JNIEnv *env, JSContextRef ctx, JSValueRef exception)
{
	if(exception)
	{
		if (JSValueIsObject(ctx, exception))
		{
			JSObjectRef obj = (JSObjectRef)exception;
			JSStringRef internalError = JSStringCreateWithUTF8CString("internalError");

			// If the JavaScript exception is an exception that was not handled, we must rethrow it
			if (JSObjectHasProperty(ctx, obj, internalError))
			{
				JSStringRef internalErrorName = JSStringCreateWithUTF8CString("internalError");
				JSValueRef internalErrPropValue = JSObjectGetProperty(ctx, obj, internalErrorName, NULL);
				intptr_t exceptionPointer = (intptr_t)JSValueToNumber(ctx, internalErrPropValue, NULL);

				JSStringRelease(internalErrorName);
				JSStringRelease(internalError);

				env->Throw((jthrowable)exceptionPointer);

				return false;
			}

			JSStringRelease(internalError);
		}

		// Otherwise, the exception is an common JavaScript error or even a String
		// (in JavaScript we can throw whatever we want). So, create an specific exception and throw it
		ThrowJavaScriptException(env, ctx, exception);

		return true;
	}

	return false;
}

/**
 * Gets the current stack trace of the JavaScript execution if a JavaScript error has been thrown.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsException The JavaScript exception thrown during evaluation.
 *
 * @return The current JavaScript stack trace.
 */
jstring GetStackTraceFromJSException(JNIEnv *env, JSContextRef ctx, JSValueRef jsException)
{
	// Getting the JavaScript stack trace from the JavaScriptCore implementation
	JSObjectRef jsExceptionObj = JSValueToObject(ctx, jsException, NULL);
	JSStringRef jsStackProperty = JSStringCreateWithUTF8CString("stack");

	jstring stackTrace = NULL;

	// If the exception object is instance of Error
	if(JSObjectHasProperty(ctx, jsExceptionObj, jsStackProperty))
	{
        JSStringRef jsStack = JSValueToStringCopy(ctx, JSObjectGetProperty(ctx, jsExceptionObj, jsStackProperty, 0), 0);
        stackTrace = JSStringToJavaString(env, jsStack);
        JSStringRelease(jsStack);
	}

	// Releasing local references
	JSStringRelease(jsStackProperty);

	return stackTrace;
}

/**
 * Throws a JavaScriptException, using the message of a JavaScript error that occured during evaluation.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsException The JavaScript exception thrown during evaluation.
 */
void ThrowJavaScriptException(JNIEnv *env, JSContextRef ctx, JSValueRef jsException)
{
	// An exception has already been thrown. Ignore this one!
	if(env->ExceptionOccurred()) {
		return;
	}

	// Gets the description of the JavaScript exception that was thrown inside the JS code.
	JSStringRef jsErrorMessage = JSValueToStringCopy(ctx, jsException, NULL);
	jstring errorMessage = JSStringToJavaString(env, jsErrorMessage);
	JSStringRelease(jsErrorMessage);

	jstring stackTrace = GetStackTraceFromJSException(env, ctx, jsException);

	env->CallStaticVoidMethod(JSCCallback.handler, JSCCallback.throwExceptionMethodID, ctx, errorMessage, stackTrace);

	env->DeleteLocalRef(errorMessage);
	env->DeleteLocalRef(stackTrace);
}
