#include "Main.h"

// Pointer to the current Java VM
JavaVM *gJavaVM;

extern "C"
{
	/*
	 * JNI initialization.
	 */
	jint JNI_OnLoad(JavaVM *vm, void *reserved)
	{
		gJavaVM = vm;
		return JNI_VERSION_1_6;
	}
}

/*
 * Gets the JNI environment from the current Java VM.
 */
JNIEnv* GetEnvironment()
{
	JNIEnv *env;
	gJavaVM->GetEnv((void**) &env, JNI_VERSION_1_6);
	return env;
}

