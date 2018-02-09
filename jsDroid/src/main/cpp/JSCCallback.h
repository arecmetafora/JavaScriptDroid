#ifndef JSCCallback_H
#define JSCCallback_H

#include "Main.h"
#include "ConversionHelper.h"
#include "ExceptionHelper.h"

// JavaScriptCore callbacks
bool callbackHasProperty(JSContextRef, JSObjectRef, JSStringRef);
JSValueRef callbackGetProperty(JSContextRef, JSObjectRef, JSStringRef, JSValueRef*);
bool callbackSetProperty(JSContextRef, JSObjectRef, JSStringRef, JSValueRef, JSValueRef*);
JSValueRef callbackFunction(JSContextRef, JSObjectRef, JSObjectRef, size_t, const JSValueRef[], JSValueRef*);
JSObjectRef callbackConstructor(JSContextRef, JSObjectRef, size_t, const JSValueRef[], JSValueRef*);
void callbackFinalize(JSObjectRef);

// Allocation
long* AllocObjectReference(JNIEnv*, jobject);
void DeallocObjectReference(JNIEnv*, JSObjectRef);
jobject GetObjectReference(JNIEnv*, JSObjectRef);

bool HasMethod(JNIEnv*, jobject, jstring);

// Struct to hold the method pointers, avoiding creating and releasing references inside the JSC callbacks
typedef struct {
	jclass handler;
	jmethodID callbackGetPropertyMethodID;
	jmethodID callbackSetPropertyMethodID;
	jmethodID callbackFunctionMethodID;
	jmethodID callbackConstructorMethodID;
	jmethodID throwExceptionMethodID;
	jmethodID allocObjectReferenceMethodID;
	jmethodID deallocObjectReferenceMethodID;
	jmethodID getObjectReferenceMethodID;
	jmethodID hasPropertyMethodID;
	jmethodID hasMethodMethodID;
	jmethodID getJSClassRefMethodID;
} _JSCCallback;
extern _JSCCallback JSCCallback;

#endif
