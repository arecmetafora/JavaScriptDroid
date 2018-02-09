#ifndef JSJni_H
#define JSJni_H

#include "Main.h"
#include "JSCCallback.h"
#include "ExceptionHelper.h"
#include "ConversionHelper.h"

// All methods mapped to be used by Java application must be declared with "extern C".
extern "C" {

JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_registerJavaScriptClass
        (JNIEnv *, jclass, jstring, jint);

JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_evaluateScript
  (JNIEnv *, jclass, jstring, jstring, jint);

JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_evaluateScriptWithParameters
  (JNIEnv *, jclass, jstring, jobjectArray, jobjectArray, jint);

JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_createJavaScriptContext
  (JNIEnv *, jclass);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_releaseJavaScriptContext
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_garbageCollect
  (JNIEnv *, jclass, jint);

JNIEXPORT jstring JNICALL Java_com_arecmetafora_jsdroid_JavaScriptDroid_getJavaScriptStackTrace
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_attach
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_detach
  (JNIEnv *, jclass, jint);

JNIEXPORT jboolean JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_isAttached
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_setBreakpoint
  (JNIEnv *, jclass, jint, jint, jint, jint, jstring, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_removeBreakpoint
  (JNIEnv *, jclass, jint, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_clearBreakpoints
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_breakProgram
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_continueProgram
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepIntoStatement
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepOverStatement
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stepOutOfFunction
  (JNIEnv *, jclass, jint);

JNIEXPORT jobject JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_evaluateScript
  (JNIEnv *, jclass, jint, jstring);

JNIEXPORT jobjectArray JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_getPropertyNamesOfCurrentScope
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_startProfiler
  (JNIEnv *, jclass, jint);

JNIEXPORT jstring JNICALL Java_com_arecmetafora_jsdroid_debug_Debugger_stopProfiler
  (JNIEnv *, jclass, jint);
}
#endif