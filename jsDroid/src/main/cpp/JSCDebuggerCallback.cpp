#include "Main.h"
#include "ExceptionHelper.h"
#include "JSCDebuggerCallback.h"

_JSCDebuggerCallbackJNI JSCDebuggerCallbackJNI;

void JSCDebuggerCallback::sourceParsed(JSContextRef ctx, const char *sourceUrl, size_t sourceID)
{
    // Gets the JNIEnv variable.
    JNIEnv *env = GetEnvironment();

    jstring javaSourceUrl = env->NewStringUTF(sourceUrl);

    env->CallStaticVoidMethod(JSCDebuggerCallbackJNI.handler, JSCDebuggerCallbackJNI.sourceParsedMethodID, ctx, javaSourceUrl, sourceID);

    env->DeleteLocalRef(javaSourceUrl);
}

void JSCDebuggerCallback::handleBreakpointHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column)
{
    // Gets the JNIEnv variable.
    JNIEnv *env = GetEnvironment();

    env->CallStaticVoidMethod(JSCDebuggerCallbackJNI.handler, JSCDebuggerCallbackJNI.handleBreakpointHitMethodID, ctx, sourceID, line, column);
}

void JSCDebuggerCallback::handleExceptionHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column, JSValueRef *exception)
{
    // Gets the JNIEnv variable.
    JNIEnv *env = GetEnvironment();

    HandleJSException(env, ctx, *exception);
    jthrowable javaException = env->ExceptionOccurred();
    env->ExceptionClear();

    env->CallStaticVoidMethod(JSCDebuggerCallbackJNI.handler, JSCDebuggerCallbackJNI.handleExceptionHitMethodID, ctx, sourceID, (int)line, (int)column, javaException);

    env->DeleteLocalRef(javaException);
}

void JSCDebuggerCallback::handleStepHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column)
{
    // Gets the JNIEnv variable.
    JNIEnv *env = GetEnvironment();

    env->CallStaticVoidMethod(JSCDebuggerCallbackJNI.handler, JSCDebuggerCallbackJNI.handleStepHitMethodID, ctx, sourceID, line, column);
}