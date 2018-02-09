#include "ConversionHelper.h"

_JavaClasses JavaClasses;

/**
 * Converts a JSValueRef (JSC) to a jobject object (Native Java).
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsValue The JavaScript object to be converted.
 *
 * @return The object converted to a native Java instance.
 */
jobject JSObjectToNative(JNIEnv *env, JSContextRef ctx, JSValueRef jsValue)
{
	jobject result = NULL;
	JSType type;

	if(jsValue)
		type = JSValueGetType(ctx, jsValue);
	else
		type = kJSTypeNull;

	switch (type)
	{
		case kJSTypeNull:
		case kJSTypeUndefined:
		default:
			break;

		case kJSTypeBoolean:
		{
			bool boolean = JSValueToBoolean(ctx, jsValue);
			result = env->NewObject(JavaClasses.Boolean, JavaClasses.booleanConstructorMethodID, boolean);
			break;
		}

		case kJSTypeNumber:
		{
			double number = JSValueToNumber(ctx, jsValue, NULL);
			result = env->NewObject(JavaClasses.Double, JavaClasses.doubleConstructorMethodID, number);
			break;
		}

		case kJSTypeString:
		{
			JSStringRef jsStr = JSValueToStringCopy(ctx, jsValue, NULL);
			result = JSStringToJavaString(env, jsStr);
			JSStringRelease(jsStr);
			break;
		}

		case kJSTypeObject:
		{
			JSObjectRef jsObj = JSValueToObject(ctx, jsValue, NULL);

			// is a mapped class instance
			if(JSObjectGetPrivate(jsObj))
			{
				result = GetObjectReference(env, jsObj);
			}
			// Is a JavaScript Date: new Date(...)
			else if(IsJSDate(ctx, jsObj))
			{
				result = ConvertJSDateToJavaDate(env, ctx, jsObj);
			}
			// Is a JavaScript Array: [1, 2, 3]
			else if(IsJSArray(ctx, jsObj))
			{
				result = (jobject)ConvertJSArrayToJavaArray(env, ctx, jsObj);
			}
			// Is a JavaScript Function (Experimental, not released! Maybe in the future...)
			else if(JSObjectIsFunction(ctx, jsObj))
			{
				// Gets the function name
				JSStringRef functionNameProperty = JSStringCreateWithUTF8CString("name");
				JSStringRef jsFunctionName = JSValueToStringCopy(ctx, JSObjectGetProperty(ctx, jsObj, functionNameProperty, 0), 0);
				jstring functionName = JSStringToJavaString(env, jsFunctionName);
				JSStringRelease(functionNameProperty);
				JSStringRelease(jsFunctionName);

				result = functionName;
			}

			// Is an any other not mapped JavaScript object
			if(!result)
			{
				// Just return a dummy object so that an error of invalid cast be thrown in the callbacks
				return env->NewObject(JavaClasses.UnmappedObject, JavaClasses.unmappedObjectConstructorMethodID);
			}

			break;
		}
	}

	return result;
}


/**
 * Converts a jobject object (Native Java) to a JSValueRef (JSC).
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsValue The native Java object to be converted.
 *
 * @return The object converted to a JavaScript instance.
 */
JSValueRef JSValueMakeWithNativeInstance(JNIEnv *env, JSContextRef ctx, jobject obj)
{
	// Fallback (not a real scenario here!): If object is null, return JavaScript null representation
	if(obj == NULL) {
		return JSValueMakeNull(ctx);
	}

	// Trying to convert the Java Native Object to a primitive representation of a JavaScript value...

	if(env->IsInstanceOf(obj, JavaClasses.Boolean))
	{
		jboolean result = env->CallBooleanMethod(obj, JavaClasses.booleanValueMethodID);
		return JSValueMakeBoolean(ctx, result);
	}
	else if(env->IsInstanceOf(obj, JavaClasses.Number))
	{
		jdouble result = env->CallDoubleMethod(obj, JavaClasses.doubleValueMethodID);
		return JSValueMakeNumber(ctx, result);
	}
	else if(env->IsInstanceOf(obj, JavaClasses.String))
	{
		const char *resultString = env->GetStringUTFChars((jstring)obj, 0);
		JSStringRef jsStr = JSStringCreateWithUTF8CString(resultString);
		JSValueRef jsResult = JSValueMakeString(ctx, jsStr);
		JSStringRelease(jsStr);
		env->ReleaseStringUTFChars((jstring)obj, resultString);
		return jsResult;
	}
	else if(env->IsInstanceOf(obj, JavaClasses.GregorianCalendar))
	{
		return ConvertJavaDateToJSDate(env, ctx, obj);
	}
	else if(env->IsInstanceOf(obj, JavaClasses.ObjectArray))
	{
		return ConvertJavaArrayToJSArray(env, ctx, (jobjectArray)obj);
	}
	else // Others classes
	{
		return JSObjectMakeWithNativeInstance(env, ctx, obj);
	}
}

/**
 * Encapsulate a native Java object instance into a JavaScriptCore object.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param obj The native Java instance to be wrapped.
 *
 * @return The reference to the JavaScript instance.
 */
JSObjectRef JSObjectMakeWithNativeInstance(JNIEnv *env, JSContextRef ctx, jobject obj)
{
	// Holds a reference to this object
	long* objHashCode = AllocObjectReference(env, obj);

	JSClassRef jsClassRef = (JSClassRef) env->CallStaticIntMethod(JSCCallback.handler, JSCCallback.getJSClassRefMethodID, obj);

	if(!env->ExceptionOccurred())
		return JSObjectMake(ctx, jsClassRef, objHashCode);
	else
		return NULL;
}

/**
 * Converts a JSStringRef (JSC string) to a jstring object (Native Java String).
 *
 * @param env Pointer to JNI environment.
 * @param jsString The JavaScript string to be converted.
 *
 * @return The string converted to a native Java instance.
 */
jstring JSStringToJavaString(JNIEnv *env, JSStringRef jsString)
{	
	size_t len = JSStringGetMaximumUTF8CStringSize(jsString);
    char *buffer = (char *)malloc(len + 1);
    JSStringGetUTF8CString(jsString, buffer, len + 1);
    jstring js = env->NewStringUTF(buffer);
    free(buffer);
    return js;	
}

/**
 * Executes a method from a JavaScript object.
 *
 * @param ctx Pointer to JavaScript context.
 * @param obj The object which the method will be called.
 * @param methodName The name of the method to be evaluated.
 *
 * @return The result of the method evaluation.
 */
JSValueRef EvatuateMethodFromJSObject(JSContextRef ctx, JSObjectRef obj, const char *methodName)
{
	JSStringRef methodNameJS = JSStringCreateWithUTF8CString(methodName);
	JSValueRef methodNameProperty = JSObjectGetProperty(ctx, obj, methodNameJS, NULL);
	JSStringRelease(methodNameJS);

	JSObjectRef methodNameObj = JSValueToObject(ctx, methodNameProperty, NULL);
	JSValueRef methodReturn = JSObjectCallAsFunction(ctx, methodNameObj, obj, 0, NULL, NULL);
	return methodReturn;
}

/**
 * Checks if a JavaScript object has a property with a given name.
 *
 * @param ctx Pointer to JavaScript context.
 * @param obj The object which the property will be checked.
 * @param propertyName The name of the property to be cheched.
 *
 * @return Whether the object contains the given property.
 */
bool HasProperty(JSContextRef ctx, JSObjectRef obj, const char* propertyName)
{
	JSStringRef jsPropertyName = JSStringCreateWithUTF8CString(propertyName);
	bool hasProperty = JSObjectHasProperty(ctx, obj, jsPropertyName);
	JSStringRelease(jsPropertyName);
	return hasProperty;
}

/**
 * Checks if a JavaScript object is instance of a Date object.
 *
 * @param ctx Pointer to JavaScript context.
 * @param obj The JavaScript object.
 *
 * @return Whether the given object is instance of a Date object.
 */
bool IsJSDate(JSContextRef ctx, JSObjectRef obj)
{
	JSValueRef proto = JSObjectGetPrototype(ctx, obj);
	JSObjectRef protoObj = JSValueToObject(ctx, proto, NULL);

	// Warning: getDay returns "day of the week"!

	return HasProperty(ctx, protoObj, "getDate") && HasProperty(ctx, protoObj, "getMonth") &&
		   HasProperty(ctx, protoObj, "getFullYear") && HasProperty(ctx, protoObj, "getHours") &&
		   HasProperty(ctx, protoObj, "getMinutes") && HasProperty(ctx, protoObj, "getSeconds");
}

/**
 * Checks if the double is a valid number.
 *
 * @param d The number to be checked.
 *
 * @return Whether the double is a number.
 */
bool IsNaN(double d)
{
	return d != d;
}

/**
 * Convert a JavaScript Date object to a native Java Date object.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsDate The JavaScript Date to be converted.
 *
 * @return The Date converted to a native Java Date instance.
 */
jobject ConvertJSDateToJavaDate(JNIEnv *env, JSContextRef ctx, JSObjectRef jsDate)
{
	JSValueRef yearValue = EvatuateMethodFromJSObject(ctx, jsDate, "getFullYear");
	JSValueRef monthValue = EvatuateMethodFromJSObject(ctx, jsDate, "getMonth");
	JSValueRef dayValue = EvatuateMethodFromJSObject(ctx, jsDate, "getDate"); // getDay returns "day of the week"!
	JSValueRef hourValue = EvatuateMethodFromJSObject(ctx, jsDate, "getHours");
	JSValueRef minuteValue = EvatuateMethodFromJSObject(ctx, jsDate, "getMinutes");
	JSValueRef secondValue = EvatuateMethodFromJSObject(ctx, jsDate, "getSeconds");

	double year = JSValueToNumber(ctx, yearValue, NULL);
	double month = JSValueToNumber(ctx, monthValue, NULL);
	double day = JSValueToNumber(ctx, dayValue, NULL);
	double hour = JSValueToNumber(ctx, hourValue, NULL);
	double minute = JSValueToNumber(ctx, minuteValue, NULL);
	double second = JSValueToNumber(ctx, secondValue, NULL);

	if(IsNaN(year) || IsNaN(month)  || IsNaN(day) || 
	   IsNaN(hour) || IsNaN(minute) || IsNaN(second))
		return NULL;

	jobject javaDate = env->NewObject(JavaClasses.GregorianCalendar, JavaClasses.gregorianCalendarConstructorMethodID,
		(jint)year, (jint)month, (jint)day, (jint)hour, (jint)minute, (jint)second);

	return javaDate;
}

/**
 * Convert a native Java Date to a JavaScript Date object.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param javaDate The native Java Date instance to be converted.
 *
 * @return The Date converted to a JavaScript Date instance.
 */
JSObjectRef ConvertJavaDateToJSDate(JNIEnv *env, JSContextRef ctx, jobject javaDate)
{	
	jint year	= env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)1);	 // YEAR
	jint month	= env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)2);	 // MONTH
	jint day	= env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)5);	 // DAY_OF_MONTH
	jint hour	= env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)11); // HOUR_OF_DAY
	jint minute = env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)12); // MINUTE
	jint second = env->CallIntMethod(javaDate, JavaClasses.gregorianCalendarGetFieldMethodID, (jint)13); // SECOND

	JSValueRef dateParts[6];
	dateParts[0] = JSValueMakeNumber(ctx, year);
	dateParts[1] = JSValueMakeNumber(ctx, month);
	dateParts[2] = JSValueMakeNumber(ctx, day);
	dateParts[3] = JSValueMakeNumber(ctx, hour);
	dateParts[4] = JSValueMakeNumber(ctx, minute);
	dateParts[5] = JSValueMakeNumber(ctx, second);
		
	JSObjectRef jsDate = JSObjectMakeDate(ctx, 6, dateParts, NULL);

	return jsDate;
}

/**
 * Checks if a JavaScript object is instance of a Array object.
 *
 * @param ctx Pointer to JavaScript context.
 * @param obj The JavaScript object.
 *
 * @return Whether the given object is instance of a Array object.
 */
bool IsJSArray(JSContextRef ctx, JSObjectRef obj)
{
	JSValueRef proto = JSObjectGetPrototype(ctx, obj);
	JSObjectRef protoObj = JSValueToObject(ctx, proto, NULL);

	return HasProperty(ctx, protoObj, "pop") && HasProperty(ctx, protoObj, "length") &&
		   HasProperty(ctx, protoObj, "push") && HasProperty(ctx, protoObj, "join");
}

/**
 * Convert a JavaScript Array object to a native Java Array object.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param jsArray The JavaScript Array to be converted.
 *
 * @return The Date converted to a native Array Date instance.
 */
jobjectArray ConvertJSArrayToJavaArray(JNIEnv *env, JSContextRef ctx, JSObjectRef jsArray)
{
	// Gets the array length
	JSStringRef lengthProperty = JSStringCreateWithUTF8CString("length");
	JSValueRef lengthValue = JSObjectGetProperty(ctx, jsArray, lengthProperty, NULL);
	JSStringRelease(lengthProperty);

	double length = JSValueToNumber(ctx, lengthValue, NULL);

	// Creating a Java Array of Objects
	jobjectArray javaArray = env->NewObjectArray((jsize)length, JavaClasses.Object, NULL);

	// Filling the Java Array with objects
	for(int i=0; i < length; i++)
	{
		// Getting the array[i] value
		JSValueRef jsArrayItem = JSObjectGetPropertyAtIndex(ctx, jsArray, (unsigned int)i, NULL);

		// Converting it to a native Java object and putting it into the object array
		jobject javaArrayItem = JSObjectToNative(env, ctx, jsArrayItem);
		env->SetObjectArrayElement(javaArray, i, javaArrayItem);
		env->DeleteLocalRef(javaArrayItem);
	}

	return javaArray;
}

/**
 * Convert a native Java Array to a JavaScript Array object.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param javaArray The native Java Array instance to be converted.
 *
 * @return The Array converted to a JavaScript Array instance.
 */
JSObjectRef ConvertJavaArrayToJSArray(JNIEnv *env, JSContextRef ctx, jobjectArray javaArray)
{	
	int length = env->GetArrayLength(javaArray);
	JSValueRef jsArrayItems[length];

	for(int i=0; i < length; i++)
	{
		jobject javaArrayItem = env->GetObjectArrayElement(javaArray, i);
		jsArrayItems[i] = JSValueMakeWithNativeInstance(env, ctx, javaArrayItem);
		env->DeleteLocalRef(javaArrayItem);

		if(env->ExceptionOccurred()) return NULL;
	}
	
	JSObjectRef jsArray = JSObjectMakeArray(ctx, (size_t)length, jsArrayItems, NULL);
	return jsArray;
}

/**
 * Creates a native Java Array within a set of JavaScript arguments.
 *
 * @param env Pointer to JNI environment.
 * @param ctx Pointer to JavaScript context.
 * @param arguments List of JavaScript object as the items of this array.
 * @param argumentCount The number of arguments sent.
 *
 * @return A new native Java Array instance with the parameters in its items.
 */
jobjectArray NewJavaArrayFromJSArguments(JNIEnv *env, JSContextRef ctx, const JSValueRef *arguments, size_t argumentCount)
{
	// Creating a Java Array of Objects
	jobjectArray javaArray = env->NewObjectArray(argumentCount, JavaClasses.Object, NULL);

	// Filling the Java Array with objects
	for(int i=0; i < argumentCount; i++)
	{
		// Converting it to a native Java object and putting it into the object array
		jobject javaArrayItem = JSObjectToNative(env, ctx, arguments[i]);
		env->SetObjectArrayElement(javaArray, i, javaArrayItem);
		env->DeleteLocalRef(javaArrayItem);
	}

	return javaArray;
}
