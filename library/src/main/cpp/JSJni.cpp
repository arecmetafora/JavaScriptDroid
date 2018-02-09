#include <JavaScriptCore/JSObjectRef.h>
#include "JSJni.h"
#include "JSCDebuggerCallback.h"
#include "ConversionHelper.h"

/**
 * Initializes all handlers for the JavaScriptCore callbacks, if needed.
 *
 * @param env Pointer to JNI environment.
 */
void InitializeCallbacksIfNeeded(JNIEnv * env) {

	if(!JSCCallback.handler) {
		jclass handler = env->FindClass("com/arecmetafora/jsdroid/JavaScriptCallback");
		JSCCallback.handler = (jclass) env->NewGlobalRef(handler);

		JSCCallback.callbackGetPropertyMethodID = 	env->GetStaticMethodID(handler, "callbackGetProperty", "(ILjava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;");
		JSCCallback.callbackSetPropertyMethodID = 	env->GetStaticMethodID(handler, "callbackSetProperty", "(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
		JSCCallback.callbackFunctionMethodID = 		env->GetStaticMethodID(handler, "callbackFunction", "(ILjava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
		JSCCallback.callbackConstructorMethodID = 	env->GetStaticMethodID(handler, "callbackConstructor", "(ILjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
		JSCCallback.throwExceptionMethodID = 		env->GetStaticMethodID(handler, "throwException", "(ILjava/lang/String;Ljava/lang/String;)V");
		JSCCallback.allocObjectReferenceMethodID = 	env->GetStaticMethodID(handler, "allocObjectReference", "(Ljava/lang/Object;)I");
		JSCCallback.deallocObjectReferenceMethodID= env->GetStaticMethodID(handler, "deallocObjectReference", "(I)V");
		JSCCallback.getObjectReferenceMethodID = 	env->GetStaticMethodID(handler, "getObjectReference", "(I)Ljava/lang/Object;");
		JSCCallback.hasPropertyMethodID = 			env->GetStaticMethodID(handler, "hasProperty", "(Ljava/lang/Object;Ljava/lang/String;)Z");
		JSCCallback.hasMethodMethodID = 			env->GetStaticMethodID(handler, "hasMethod", "(Ljava/lang/Object;Ljava/lang/String;)Z");
		JSCCallback.getJSClassRefMethodID = 		env->GetStaticMethodID(handler, "getJSClassRef", "(Ljava/lang/Object;)I");
	}

	if(!JSCDebuggerCallbackJNI.handler) {
		jclass handler = env->FindClass("com/arecmetafora/jsdroid/debugger/DebuggerCallback");
		JSCDebuggerCallbackJNI.handler = (jclass) env->NewGlobalRef(handler);

		JSCDebuggerCallbackJNI.sourceParsedMethodID			= 	env->GetStaticMethodID(handler, "sourceParsed", "(ILjava/lang/String;I)V");
		JSCDebuggerCallbackJNI.handleBreakpointHitMethodID	= 	env->GetStaticMethodID(handler, "handleBreakpointHit", "(IIII)V");
		JSCDebuggerCallbackJNI.handleExceptionHitMethodID	= 	env->GetStaticMethodID(handler, "handleExceptionHit", "(IIIILjava/lang/Throwable;)V");
		JSCDebuggerCallbackJNI.handleStepHitMethodID		= 	env->GetStaticMethodID(handler, "handleStepHit", "(IIII)V");
	}

	if(!JavaClasses.String) {
		JavaClasses.Boolean = 			(jclass)env->NewGlobalRef(env->FindClass("java/lang/Boolean"));
		JavaClasses.Double = 			(jclass)env->NewGlobalRef(env->FindClass("java/lang/Double"));
		JavaClasses.Number = 			(jclass)env->NewGlobalRef(env->FindClass("java/lang/Number"));
		JavaClasses.String = 			(jclass)env->NewGlobalRef(env->FindClass("java/lang/String"));
		JavaClasses.GregorianCalendar = (jclass)env->NewGlobalRef(env->FindClass("java/util/GregorianCalendar"));
		JavaClasses.Object = 			(jclass)env->NewGlobalRef(env->FindClass("java/lang/Object"));
		JavaClasses.ObjectArray = 		(jclass)env->NewGlobalRef(env->FindClass("[Ljava/lang/Object;"));
		JavaClasses.UnmappedObject = 	(jclass)env->NewGlobalRef(env->FindClass("com/arecmetafora/jsdroid/UnmappedObject"));
		JavaClasses.Exception = 		(jclass)env->NewGlobalRef(env->FindClass("java/lang/Throwable"));
		JavaClasses.booleanConstructorMethodID = env->GetMethodID(JavaClasses.Boolean, "<init>", "(Z)V");
		JavaClasses.doubleConstructorMethodID = env->GetMethodID(JavaClasses.Double, "<init>", "(D)V");
		JavaClasses.unmappedObjectConstructorMethodID = env->GetMethodID(JavaClasses.UnmappedObject, "<init>", "()V");
		JavaClasses.gregorianCalendarConstructorMethodID = env->GetMethodID(JavaClasses.GregorianCalendar, "<init>", "(IIIIII)V");
		JavaClasses.booleanValueMethodID = env->GetMethodID(JavaClasses.Boolean, "booleanValue", "()Z");
		JavaClasses.doubleValueMethodID = env->GetMethodID(JavaClasses.Number, "doubleValue", "()D");
		JavaClasses.gregorianCalendarGetFieldMethodID = env->GetMethodID(JavaClasses.GregorianCalendar, "get", "(I)I");
	}
}

/**
 * Checks the syntax of a JavaScript content.
 *
 * @param env Pointer to JNI environment.
 * @param jsScript JavaScript to be checked.
 * @param ctx The execution context to use.
 *
 * @returns true if the JavaScript syntax is correct, of false otherwise.
 */
bool CheckScriptSyntax(JNIEnv *env, JSStringRef jsScript, JSContextRef ctx)
{
	JSValueRef exception = 0;
	JSCheckScriptSyntax(ctx, jsScript, 0, 0, &exception);
	if (exception)
	{
		ThrowJavaScriptException(env, ctx, exception);
		return false;
	}
	return true;
}

/**
 * Register a class in a JavaScript context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param jsName The name of the class to be registered.
 * @param contextPointer The pointer to the JavaScript context which the class is being registered to.
 *
 * @return The reference to the JavaScript class definition, so this class can be instantiated later by JavaScript engine execution.
 */
JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_registerJavaScriptClass
        (JNIEnv * env, jclass cls, jstring jsName, jint contextPointer)
{
    InitializeCallbacksIfNeeded(env);

    const char *jsNameChars = env->GetStringUTFChars(jsName, 0);
    JSContextRef ctx = (JSContextRef) contextPointer;

    JSObjectRef jsGlobalContext = JSContextGetGlobalObject(ctx);

    // Create the JavaScriptCore class definition
    JSClassDefinition definition = kJSClassDefinitionEmpty;
    definition.attributes = kJSClassAttributeNone;
    definition.className = jsNameChars;
    definition.hasProperty = callbackHasProperty;
    definition.getProperty = callbackGetProperty;
    definition.setProperty = callbackSetProperty;
    definition.finalize = callbackFinalize;
    JSClassRef jsClassRef = JSClassCreate(&definition);

    // Creates the constructor definition
    JSObjectRef constructorRef = JSObjectMakeConstructor(ctx, jsClassRef, (JSObjectCallAsConstructorCallback) callbackConstructor);

    // Register the JSClassRef and its constructor in the global context
    JSStringRef jsClassName = JSStringCreateWithUTF8CString(jsNameChars);
    JSObjectSetProperty(ctx, jsGlobalContext, jsClassName, constructorRef, kJSPropertyAttributeDontEnum, NULL);

    // Storing the class name into a property of the constructor
    JSStringRef nameProperty = JSStringCreateWithUTF8CString("name");
    JSObjectSetProperty(ctx, constructorRef, nameProperty, JSValueMakeString(ctx, jsClassName), kJSPropertyAttributeReadOnly, NULL);
    JSStringRelease(nameProperty);
    JSStringRelease(jsClassName);

    env->ReleaseStringUTFChars(jsName, jsNameChars);

    // Returns the pointer to the JavaScript class definition to the Java layer,
    // so that a new object can be instantiated in the callback of functions, gets and constructors
    return (intptr_t)jsClassRef;
}

/**
 * Evaluates a JavaScript.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param script The script to be evaluated.
 * @param scriptName The name of the file which this script was retrieved.
 * @param contextPointer The context which the script will be executed.
 *
 * @return The result of the evaluation.
 */
JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_evaluateScript
        (JNIEnv *env, jclass cls, jstring script, jstring scriptName,  jint contextPointer){

	JSValueRef exception = 0;
	JSContextRef ctx = (JSContextRef) contextPointer;
			
	// Converts the script as a JSC string variable
	const char* strScript = (env)->GetStringUTFChars(script , NULL);
	JSStringRef scriptJS = JSStringCreateWithUTF8CString(strScript);
	env->ReleaseStringUTFChars(script, strScript);
	
	// Checks if there is syntax errors in the script
	if(!CheckScriptSyntax(env, scriptJS, ctx))
	{
		JSStringRelease(scriptJS);
		return NULL;
	}

	// Converts the script name as a JSC string variable
	JSStringRef scriptNameJS = 0;
	if(scriptName) {
		const char* strScriptName = (env)->GetStringUTFChars(scriptName , NULL);
		scriptNameJS = JSStringCreateWithUTF8CString(strScriptName);
		env->ReleaseStringUTFChars(scriptName, strScriptName);
	}

	// Evaluates the script in the JavaScript context
    JSValueRef result = JSEvaluateScript(ctx, scriptJS, 0, scriptNameJS, 0, &exception);

	// Releasing local variables
	JSStringRelease(scriptJS);
	if(scriptNameJS) {
		JSStringRelease(scriptNameJS);
	}

	// Check if some error was thrown during the script evaluation
	if(!HandleJSException(env, ctx, exception))
		// if not, convert the result to a native object and return it to Java layer
		return JSObjectToNative(env, ctx, result);
	else
		// Otherwise, ignore, returning null, since the exception was rethrown in the error checking above
		return NULL;
}

/**
 * Evaluates an anonymous JavaScript function, which arguments are supplied by parameters.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param script A JavaScript to be executed.
 * @param paramsNames The name of function arguments.
 * @param paramsValues The argument values.
 * @param jsContextPointer The context which the script will be executed.
 *
 * @return The result of the evaluation.
 */
JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_evaluateScriptWithParameters
  (JNIEnv *env, jclass cls, jstring script, jobjectArray paramsNames, jobjectArray paramsValues, jint contextPointer)
{
	JSContextRef ctx = (JSContextRef) contextPointer;

	unsigned int paramsCount = (unsigned int)env->GetArrayLength(paramsNames);
	JSStringRef jsParamNames[paramsCount];
	JSValueRef jsValues[paramsCount];
	
	// Converts the script as a JSC string variable
	const char* strScript = env->GetStringUTFChars(script, 0);
	JSStringRef scriptJS = JSStringCreateWithUTF8CString(strScript); 
	env->ReleaseStringUTFChars(script, strScript);
	
	// Creates the JavaScript parameter names with the native parameters from native call
	for (int i = 0; i < paramsCount; i++) {
		jstring javaParamName = (jstring)env->GetObjectArrayElement(paramsNames, i);
		const char* paramName = (env)->GetStringUTFChars(javaParamName, 0);
		jsParamNames[i] = JSStringCreateWithUTF8CString(paramName);
		env->ReleaseStringUTFChars(javaParamName, paramName);
	}

	JSValueRef exception = 0;

	// Creates a JavaScript function with the parameters
	JSObjectRef fn = JSObjectMakeFunction(ctx, 0, paramsCount, jsParamNames, scriptJS, 0, 1, &exception);

	// Releases all parameter names
	for (int i = 0; i < paramsCount; i++) {
        JSStringRelease(jsParamNames[i]);
    }

	// Fallback: If a syntax error occurred during the function parse/creation, abort the function call
	if(HandleJSException(env, ctx, exception))
	{
		JSStringRelease(scriptJS);
		return NULL;
	}

	// Converts the native parameters to JavaScript parameters
	for (int i = 0; i  < paramsCount; i++)  {
		// Encapsulating the native Java object to a JavaScript instance
		jsValues[i] = JSValueMakeWithNativeInstance(env, ctx, env->GetObjectArrayElement(paramsValues, i));

		if(env->ExceptionOccurred())
		{
			JSStringRelease(scriptJS);
			return NULL;
		}
	}

	// Execute the JavaScript function
	JSValueRef result = JSObjectCallAsFunction(ctx, fn, 0, paramsCount, jsValues, &exception);

	// Releasing local variables
	JSStringRelease(scriptJS);

	// Check if some error was thrown during the script evaluation
	if(!HandleJSException(env, ctx, exception))
		// if not, convert the result to a native object and return it to Java layer
		return JSObjectToNative(env, ctx, result);
	else
		// Otherwise, ignore, returning null, since the exception was rethrown in the error checking above
		return NULL;
}

/**
 * Create a new JavaScript context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 *
 * @return The pointer to the created JavaScript context.
 */
JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_createJavaScriptContext
  (JNIEnv *env, jclass cls)
{
	InitializeCallbacksIfNeeded(env);
	return (intptr_t) JSGlobalContextCreate(NULL);
}

/**
 * Releases a JavaScript context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer The JavaScript context to be released.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_releaseJavaScriptContext
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSGlobalContextRelease((JSGlobalContextRef) contextPointer);
}

/**
 * Call the JavaScript garbage collector execution in specific context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer The JavaScript context which the objects will be collected.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_garbageCollect
  (JNIEnv * env, jclass cls,  jint contextPointer)
{
	JSGarbageCollect((JSContextRef) contextPointer);
}

/**
 * Gets the stack trace of the current JavaScript execution from global context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer Pointer to the JavaScript context which the stack trace will be obtained.
 *
 * @return The stack trace of the current JavaScript execution.
 */
JNIEXPORT jstring JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_getJavaScriptStackTrace
  (JNIEnv * env, jclass cls, jint contextPointer)
{
	JSStringRef javaScriptStackTrace = JSContextCreateBacktrace((JSContextRef) contextPointer, 10);
	jstring stackTrace = JSStringToJavaString(env, javaScriptStackTrace);
	JSStringRelease(javaScriptStackTrace);
	return stackTrace;
}

/**
 * Attaches the debugger to a context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer The context in which the debugger will be attached.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_attach
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerAttach((JSContextRef) contextPointer, (JSDebuggerCallback *) new JSCDebuggerCallback());
}

/**
 * Detaches the debugger from a context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer The context in which the debugger will be detached.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_detach
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerDetach((JSContextRef) contextPointer);
}

/**
 * Checks if the debugger is attached to a context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer The context in which the debugger is detached to.
 *
 * @return Whether the debugger is attached to the context.
 */
JNIEXPORT jboolean JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_isAttached
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	return (jboolean) JSDebuggerIsAttached((JSContextRef) contextPointer);
}

/**
 * Sets a debugger breakpoint.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param jsContextPointer A pointer to the JavaScript context.
 * @param sourceID The JavaScript source.
 * @param line The breakpoint line number.
 * @param column The breakpoint column number.
 * @param condition A JavaScript expression representing the breakpoint condition.
 * @param hitCount Number of times the breakpoint should be hit before breaking execution.
 *
 * @return The breakpoint identifier.
 */
JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_setBreakpoint
  (JNIEnv *env, jclass cls, jint contextPointer, jint sourceID, jint line, jint column, jstring condition, jint hitCount)
{
	const char *conditionChars = env->GetStringUTFChars(condition, 0);

	jint breakpointID = JSDebuggerSetBreakpoint((JSContextRef) contextPointer, (size_t)sourceID, (size_t)line, (size_t)column, conditionChars, (size_t)hitCount);

	env->ReleaseStringUTFChars(condition, conditionChars);

	return breakpointID;
}

/**
 * Removes a debugger breakpoint.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param jsContextPointer A pointer to the JavaScript context.
 * @param breakpointID The breakpoint identifier.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_removeBreakpoint
  (JNIEnv *env, jclass cls, jint contextPointer, jint breakpointID)
{
	JSDebuggerRemoveBreakpoint((JSContextRef) contextPointer, (size_t)breakpointID);
}

/**
 * Clear all breakpoints.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_clearBreakpoints
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerClearBreakpoints((JSContextRef) contextPointer);
}

/**
 * Clear all breakpoints.
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_breakProgram
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerBreakProgram((JSContextRef) contextPointer);
}

/**
 * Breaks the JavaScript execution.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_continueProgram
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerContinueProgram((JSContextRef) contextPointer);
}

/**
 * Continues the JavaScript execution.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepIntoStatement
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerStepIntoStatement((JSContextRef) contextPointer);
}

/**
 * Proceed the debugging to the next statement, over the current statement.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepOverStatement
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerStepOverStatement((JSContextRef) contextPointer);
}

/**
 * Proceed the debugging to the next statement, out of the current scope.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepOutOfFunction
  (JNIEnv *env, jclass cls, jint contextPointer)
{
	JSDebuggerStepOutOfFunction((JSContextRef) contextPointer);
}

/**
 * Evaluates a JavaScript expression under the current debugging context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 * @param script The JavaScript to be evaluated.
 *
 * @return The result of the JavaScript evaluation.
 */
JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_evaluateScript
  (JNIEnv *env, jclass cls, jint contextPointer, jstring script)
{
	JSValueRef exception = 0;
	JSContextRef ctx = (JSContextRef) contextPointer;

	// Converts the script as a JSC string variable
	const char* strScript = (env)->GetStringUTFChars(script , NULL);
	JSStringRef scriptJS = JSStringCreateWithUTF8CString(strScript);
	env->ReleaseStringUTFChars(script, strScript);

	// Checks if there is syntax errors in the script
	if(!CheckScriptSyntax(env, scriptJS, ctx))
	{
		JSStringRelease(scriptJS);
		return NULL;
	}

	// Evaluates the script in the JavaScript context
	JSValueRef result = JSDebuggerEvaluateScript(ctx, scriptJS, &exception);

	// Releasing local variables
	JSStringRelease(scriptJS);

	// Check if some error was thrown during the script evaluation
	if(!HandleJSException(env, ctx, exception))
		// if not, convert the result to a native object and return it to Java layer
		return JSObjectToNative(env, ctx, result);
	else
		// Otherwise, ignore, returning null, since the exception was rethrown in the error checking above
		return NULL;
}

/**
 * Gets the name of all properties from current context.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 *
 * @return The property names of the current debugger scope.
 */
JNIEXPORT jobjectArray JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_getPropertyNamesOfCurrentScope
  (JNIEnv *env, jclass cls, jint contextPointer)
{
    // Gets the JavaScript variables from the current debugger scope
    JSPropertyNameArrayRef jsVariablesArray = JSDebuggerGetPropertyNamesOfCurrentScope((JSContextRef) contextPointer);

    size_t size = JSPropertyNameArrayGetCount(jsVariablesArray);
    jobjectArray variablesArray = env->NewObjectArray(size, JavaClasses.String, NULL);

    for(int i=0; i < size; i++) {
        // Convert each JavaScript variable to native Java object
        JSStringRef jsVariable = JSPropertyNameArrayGetNameAtIndex(jsVariablesArray, (size_t)i);
        jobject javaArrayItem = JSStringToJavaString(env, jsVariable);
        env->SetObjectArrayElement(variablesArray, i, javaArrayItem);
        env->DeleteLocalRef(javaArrayItem);
        JSStringRelease(jsVariable);
    }

    return variablesArray;
}

/**
 * Starts the JavaScript profiler.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 */
JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_startProfiler
	(JNIEnv *env, jclass cls, jint contextPointer) {

	JSDebuggerStartProfiler((JSContextRef) contextPointer);
}

/**
 * Stops the JavaScript profiler.
 *
 * @param env Pointer to JNI environment.
 * @param cls Class which this call was originated.
 * @param contextPointer A pointer to the JavaScript context.
 *
 * @return A JSON String containing the profiling data.
 */
JNIEXPORT jstring JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stopProfiler
	(JNIEnv *env, jclass cls, jint contextPointer) {

	const char* profilingResult = JSDebuggerStopProfiler((JSContextRef) contextPointer);

	return env->NewStringUTF(profilingResult);
}