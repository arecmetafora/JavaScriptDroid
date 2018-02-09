#ifndef JSCDebuggerCallback_H
#define JSCDebuggerCallback_H

#include <JavaScriptCore/JSDebugger.h>
#include <jni.h>

class JSCDebuggerCallback : JSDebuggerCallback {

public:
    void sourceParsed(JSContextRef ctx, const char *sourceUrl, size_t sourceID);
    void handleBreakpointHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column);
    void handleExceptionHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column, JSValueRef *exception);
    void handleStepHit(JSContextRef ctx, size_t sourceID, unsigned line, unsigned column);
};

// Struct to hold the method pointers, avoiding creating and releasing references inside the JSC callbacks
typedef struct {
    jclass handler;
    jmethodID sourceParsedMethodID;
    jmethodID handleBreakpointHitMethodID;
    jmethodID handleExceptionHitMethodID;
    jmethodID handleStepHitMethodID;
} _JSCDebuggerCallbackJNI;
extern _JSCDebuggerCallbackJNI JSCDebuggerCallbackJNI;

#endif // JSCDebuggerCallback_H