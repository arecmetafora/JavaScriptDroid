#ifndef ExceptionHelper_H
#define ExceptionHelper_H

#include "Main.h"
#include "ConversionHelper.h"

bool HandleJavaException(JNIEnv*, JSContextRef, JSValueRef*);
bool HandleJSException(JNIEnv*, JSContextRef, JSValueRef);

void ThrowJavaScriptException(JNIEnv*, JSContextRef, JSValueRef);

#endif
