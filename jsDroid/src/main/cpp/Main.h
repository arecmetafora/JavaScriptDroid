#ifndef main_H
#define main_H

#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include <JavaScriptCore/JavaScript.h>

#define LogError(message) __android_log_write(ANDROID_LOG_ERROR, "JSDroid", message)
#define LogInfo(message) __android_log_write(ANDROID_LOG_INFO, "JSDroid", message)

JNIEnv* GetEnvironment();

#endif
