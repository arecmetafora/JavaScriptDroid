package com.arecmetafora.jsdroid;

import java.util.GregorianCalendar;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JavaScriptGetPropertyTests extends InstrumentationTestCase {

	@Before
	public void registerClass() {
		JavaScriptDroid.registerClass(JSObject.class);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T callScript(String script, JSObject obj, Class<T> returnClazz)
			throws JavaScriptException {
		return (T) JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "obj" }, obj);
	}

	@Test
	public void getPropertyDouble() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.doubleValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getDouble(), result.doubleValue());

		script = "return obj['doubleValue'];";
		result = callScript(script, obj, Number.class);
		assertEquals(obj.getDouble(), result.doubleValue());
	}

	@Test
	public void getPropertyBoolean() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.booleanValue;";
		Boolean result = callScript(script, obj, Boolean.class);
		assertEquals(obj.getBoolean(), result.booleanValue());
	}

	@Test
	public void getPropertyInt() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.intValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getInt(), result.intValue());
	}

	@Test
	public void getPropertyFloat() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.floatValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getFloat(), result.floatValue());
	}

	@Test
	public void getPropertyByte() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.byteValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getByte(), result.byteValue());
	}

	@Test
	public void getPropertyShort() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.shortValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getShort(), result.shortValue());
	}

	@Test
	public void getPropertyLong() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.longValue;";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getLong(), result.longValue());
	}

	@Test
	public void getPropertyNumber() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.numberValue;";
		Object result = callScript(script, obj, Number.class);
		assertEquals(obj.getNumber(), result);
	}

	@Test
	public void getPropertyString() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.stringValue;";
		Object result = callScript(script, obj, String.class);
		assertEquals(obj.getString(), result);
	}

	@Test
	public void getPropertyDate() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.dateValue;";
		Object result = callScript(script, obj, Number.class);
		assertEquals(obj.getDate(), result);
	}

	@Test
	public void getPropertyArray() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.arrayValue;";
		Object[] result = callScript(script, obj, Object[].class);
		assertNotNull(obj.getArray());
		assertEquals(obj.getArray().length, result.length);
		assertEquals(obj.getArray()[0], ((Number)result[0]).doubleValue());
		assertEquals(obj.getArray()[1], result[1]);
		assertEquals(obj.getArray()[2], result[2]);
	}

	@Test
	public void getPropertyNull() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.objectValue;";
		Object result = callScript(script, obj, Object.class);
		assertNull(result);
	}

	@Test
	public void getPropertyObject() throws JavaScriptException {
		JSObject obj = new JSObject();
		obj.setObject(obj);

		String script = "return obj.objectValue;";
		Object result = callScript(script, obj, Object.class);
		assertEquals(obj.getObject(), result);
		assertSame(obj, result);
	}

	@Test
	public void getPropertyNotMapped() throws JavaScriptException {
		JSObject obj = new JSObject();
		String script = "return obj.notMappedProperty;";
		Object result = callScript(script, obj, Object.class);
		assertNull(result);
	}
	
	// These Stress tests is a not a performance tests. It guarantees that
	// all JNI references are being released properly
	@Test
	public void getPropertyStress() throws JavaScriptException {
		JSObject obj = new JSObject();
		obj.setObject(obj);
		obj.setArray(new Object[] { false, 1, new GregorianCalendar(), obj });

		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   var a = obj.numberValue;"
				+ "	   var b = obj.stringValue;"
				+ "    var d = obj.dateValue;"
				+ "    var e = obj.arrayValue;"
				+ "	   var c = obj.objectValue;"
				+ "}";
		
		callScript(script, obj, Object.class);
	}
}
