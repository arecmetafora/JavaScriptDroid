package com.arecmetafora.jsdroid;

import com.arecmetafora.jsdroid.annotation.JavaScriptMapped;
import com.arecmetafora.jsdroid.annotation.NonNull;

import java.util.Date;
import java.util.GregorianCalendar;

public class JSObject {

	@JavaScriptMapped
	public double doubleValue = Double.MAX_VALUE;
	@JavaScriptMapped
	public boolean booleanValue = true;
	@JavaScriptMapped
	public int intValue = Integer.MAX_VALUE;
	@JavaScriptMapped
	public float floatValue = Float.MAX_VALUE;
	@JavaScriptMapped
	public byte byteValue = Byte.MAX_VALUE;
	@JavaScriptMapped
	public short shortValue = Short.MAX_VALUE;
	@JavaScriptMapped
	public long longValue = Long.MAX_VALUE;
	@NonNull
	@JavaScriptMapped
	public Number numberValue = Double.MAX_VALUE;
	@NonNull
	@JavaScriptMapped
	public String stringValue = "JavaScript";
	@NonNull
	@JavaScriptMapped
	public GregorianCalendar dateValue = new GregorianCalendar(2012, 31, 12, 23, 58, 59);
	@NonNull
	@JavaScriptMapped
	public Object[] arrayValue = new Object[] { 1d, "2", true };
	@NonNull
	@JavaScriptMapped
	public JSObject objectValue;
	@JavaScriptMapped
	public JSObject nullableObject;
	
	@JavaScriptMapped
	public JSObject() {
	}

	@JavaScriptMapped
	public double getDouble() {
		return doubleValue;
	}

	@JavaScriptMapped
	public void setDouble(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
	@JavaScriptMapped
	public boolean getBoolean() {
		return booleanValue;
	}
	
	@JavaScriptMapped
	public void setBoolean(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@JavaScriptMapped
	public int getInt() {
		return intValue;
	}

	@JavaScriptMapped
	public void setInt(int intValue) {
		this.intValue = intValue;
	}

	@JavaScriptMapped
	public float getFloat() {
		return floatValue;
	}

	@JavaScriptMapped
	public void setFloat(float floatValue) {
		this.floatValue = floatValue;
	}

	@JavaScriptMapped
	public byte getByte() {
		return byteValue;
	}

	@JavaScriptMapped
	public void setByte(byte byteValue) {
		this.byteValue = byteValue;
	}

	@JavaScriptMapped
	public short getShort() {
		return shortValue;
	}

	@JavaScriptMapped
	public void setShort(short shortValue) {
		this.shortValue = shortValue;
	}

	@JavaScriptMapped
	public long getLong() {
		return longValue;
	}

	@JavaScriptMapped
	public void setLong(long longValue) {
		this.longValue = longValue;
	}

	@JavaScriptMapped
	public Number getNumber() {
		return numberValue;
	}

	@JavaScriptMapped
	public void setNumber(@NonNull Number numberValue) {
		this.numberValue = numberValue;
	}

	@JavaScriptMapped
	public String getString() {
		return stringValue;
	}
	
	@JavaScriptMapped
	public void setString(@NonNull String stringValue) {
		this.stringValue = stringValue;
	}
	
	@JavaScriptMapped
	public GregorianCalendar getDate() {
		return dateValue;
	}

	@JavaScriptMapped
	public void setDate(@NonNull GregorianCalendar dateValue) {
		this.dateValue = dateValue;
	}

	@JavaScriptMapped
	public Object[] getArray() {
		return arrayValue;
	}

	@JavaScriptMapped
	public void setArray(@NonNull Object[] arrayValue) {
		this.arrayValue = arrayValue;
	}

	@JavaScriptMapped
	public JSObject getObject() {
		return objectValue;
	}

	@JavaScriptMapped
	public void setObject(@NonNull JSObject objectValue) {
		this.objectValue = objectValue;
	}

	@JavaScriptMapped
	public void setNullableObject(JSObject objectValue) {
		this.objectValue = objectValue;
	}

	@JavaScriptMapped
	public Date getUnmappedObject() {
		return new Date();
	}

	@JavaScriptMapped
	public void throwException() throws Exception {
		throw new JavaScriptException("Exception Test");
	}

	@JavaScriptMapped
	public void withParams(Number n, Boolean b, String s, GregorianCalendar d, Object[] a, JSObject o) {
		this.numberValue = n;
		this.booleanValue = b;
		this.stringValue = s;
		this.dateValue = d;
		this.arrayValue = a;
		this.objectValue = o;
	}
	
	@JavaScriptMapped
	@SuppressWarnings("null")
	public void throwNullPointerException() {
		Object obj = null;
		obj.toString(); 
	}
}

@JavaScriptMapped(name = "JSObjectDouble")
class JSObjectDoubleConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectDoubleConstructor(double doubleValue) {
		setDouble(doubleValue);
	}
}

@JavaScriptMapped(name = "JSObjectBoolean")
class JSObjectBooleanConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectBooleanConstructor(boolean booleanValue) {
		setBoolean(booleanValue);
	}
}

@JavaScriptMapped(name = "JSObjectInt")
class JSObjectIntConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectIntConstructor(int intValue) {
		setInt(intValue);
	}
}

@JavaScriptMapped(name = "JSObjectFloat")
class JSObjectFloatConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectFloatConstructor(float floatValue) {
		setFloat(floatValue);
	}
}

@JavaScriptMapped(name = "JSObjectByte")
class JSObjectByteConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectByteConstructor(byte byteValue) {
		setByte(byteValue);
	}
}

@JavaScriptMapped(name = "JSObjectShort")
class JSObjectShortConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectShortConstructor(short shortValue) {
		setShort(shortValue);
	}
}

@JavaScriptMapped(name = "JSObjectLong")
class JSObjectLongConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectLongConstructor(long longValue) {
		setLong(longValue);
	}
}

@JavaScriptMapped(name = "JSObjectNumber")
class JSObjectNumberConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectNumberConstructor(@NonNull Number numberValue) {
		setNumber(numberValue);
	}
}

@JavaScriptMapped(name = "JSObjectString")
class JSObjectStringConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectStringConstructor(@NonNull String stringValue) {
		setString(stringValue);
	}
}

@JavaScriptMapped(name = "JSObjectDate")
class JSObjectDateConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectDateConstructor(@NonNull GregorianCalendar dateValue) {
		setDate(dateValue);
	}
}

@JavaScriptMapped(name = "JSObjectArray")
class JSObjectArrayConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectArrayConstructor(@NonNull Object[] arrayValue) {
		setArray(arrayValue);
	}
}

@JavaScriptMapped(name = "JSObjectObject")
class JSObjectObjectConstructor extends JSObject {
	@JavaScriptMapped()
	public JSObjectObjectConstructor(@NonNull JSObject objectValue) {
		setObject(objectValue);
	}
}

@JavaScriptMapped(name = "JSObjectNullableObject")
class JSObjectNullableObject extends JSObject {
	@JavaScriptMapped()
	public JSObjectNullableObject(JSObject objectValue) {
		setObject(objectValue);
	}
}

@JavaScriptMapped(name = "JSObjectUnmapped")
class JSObjectUnmappedConstructor extends JSObject {
}

@JavaScriptMapped(name = "JSObjectException")
class JSObjectExceptionConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectExceptionConstructor() throws Exception {
		throwException();
	}
}

@JavaScriptMapped(name = "JSObjectNullPointer")
class JSObjectNullPointerConstructor extends JSObject {
	@JavaScriptMapped
	public JSObjectNullPointerConstructor() {
		throwNullPointerException();
	}
}

@JavaScriptMapped(name = "JSObjectParams")
class JSObjectConstructorWithParams extends JSObject {
	@JavaScriptMapped
	public JSObjectConstructorWithParams(Number n, Boolean b, String s, GregorianCalendar d, Object[] a, JSObject o) {
		withParams(n, b, s, d, a, o);
	}
}