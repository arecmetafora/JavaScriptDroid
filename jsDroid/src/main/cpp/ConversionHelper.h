#ifndef ConversionHelper_H
#define ConversionHelper_H

#include "Main.h"
#include "JSCCallback.h"

// JavaScript/Native conversions
jobject JSObjectToNative(JNIEnv*, JSContextRef, JSValueRef);
JSValueRef JSValueMakeWithNativeInstance(JNIEnv*, JSContextRef, jobject);
JSObjectRef JSObjectMakeWithNativeInstance(JNIEnv*, JSContextRef, jobject);

// Date - type checking & conversion
bool IsJSDate(JSContextRef, JSObjectRef);
jobject ConvertJSDateToJavaDate(JNIEnv*, JSContextRef, JSObjectRef);
JSObjectRef ConvertJavaDateToJSDate(JNIEnv*, JSContextRef, jobject);

// Array - type checking & conversion
bool IsJSArray(JSContextRef ctx, JSObjectRef obj);
jobjectArray ConvertJSArrayToJavaArray(JNIEnv*, JSContextRef, JSObjectRef);
JSObjectRef ConvertJavaArrayToJSArray(JNIEnv*, JSContextRef, jobjectArray);
jobjectArray NewJavaArrayFromJSArguments(JNIEnv*, JSContextRef, const JSValueRef*, size_t);

jstring JSStringToJavaString(JNIEnv*, JSStringRef);

// Struct to hold the most used classes to avoid instantiate it every time
typedef struct {
	jclass Boolean;
	jclass Double;
	jclass Number;
	jclass String;
	jclass GregorianCalendar;
	jclass Object;
	jclass ObjectArray;
	jclass UnmappedObject;
	jclass Exception;
	jmethodID booleanConstructorMethodID;
	jmethodID doubleConstructorMethodID;
	jmethodID unmappedObjectConstructorMethodID;
	jmethodID gregorianCalendarConstructorMethodID;
	jmethodID booleanValueMethodID;
	jmethodID doubleValueMethodID;
	jmethodID gregorianCalendarGetFieldMethodID;
} _JavaClasses;
extern _JavaClasses JavaClasses;

#endif
