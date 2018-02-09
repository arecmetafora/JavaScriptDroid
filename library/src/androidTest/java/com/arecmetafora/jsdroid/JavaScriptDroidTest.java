package com.arecmetafora.jsdroid;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class JavaScriptDroidTest {

	@Test
	public void registerClass() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject cls = new JSObject();

		String script = "return cls";
		Object result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "cls" }, cls);
		assertNotNull(result);
		assertSame(cls, result);
	}

	@Test
	public void evaluateScriptReturningPrimitives() throws JavaScriptException {
		Object result = JavaScriptDroid.evaluateScript("true");
		assertNotNull(result);
		assertTrue(result instanceof Boolean);
		assertTrue((Boolean) result);

		result = JavaScriptDroid.evaluateScript("2");
		assertNotNull(result);
		assertTrue(result instanceof Number);
		assertEquals(2.0, ((Number) result).doubleValue(), 0);

		result = JavaScriptDroid.evaluateScript("'STR'");
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals("STR", result);
	}

	@Test
	public void evaluateScriptWithParametersReturningPrimitives() throws JavaScriptException {
		Object result = JavaScriptDroid.evaluateScriptWithParameters("return true", new String[0]);
		assertNotNull(result);
		assertTrue(result instanceof Boolean);
		assertTrue((Boolean) result);

		result = JavaScriptDroid.evaluateScriptWithParameters("return 2", new String[0]);
		assertNotNull(result);
		assertTrue(result instanceof Number);
		assertEquals(2.0, ((Number) result).doubleValue(), 0);

		result = JavaScriptDroid.evaluateScriptWithParameters("return 'STR'", new String[0]);
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals("STR", result);
	}

	@Test
	public void evaluateScriptReturningDate() throws JavaScriptException {
		// 31/12/2012 23:59:58
		String script = "var date = new Date(2012, 12, 31, 23, 59, 58); date;";

		Object result = JavaScriptDroid.evaluateScript(script);
		assertTrue(result instanceof GregorianCalendar);

		GregorianCalendar jsdate = (GregorianCalendar) result;
		GregorianCalendar javaDate = new GregorianCalendar(2012, 12, 31, 23, 59, 58);

		assertEquals(javaDate.get(Calendar.YEAR), jsdate.get(Calendar.YEAR));
		assertEquals(javaDate.get(Calendar.MONTH), jsdate.get(Calendar.MONTH));
		assertEquals(javaDate.get(Calendar.DAY_OF_MONTH), jsdate.get(Calendar.DAY_OF_MONTH));
		assertEquals(javaDate.get(Calendar.HOUR_OF_DAY), jsdate.get(Calendar.HOUR_OF_DAY));
		assertEquals(javaDate.get(Calendar.MINUTE), jsdate.get(Calendar.MINUTE));
		assertEquals(javaDate.get(Calendar.SECOND), jsdate.get(Calendar.SECOND));
	}

	@Test
	public void evaluateScriptWithParametersReturningDate() throws JavaScriptException {
		// 31/12/2012 23:59:58
		String script = "var date = new Date(2012, 12, 31, 23, 59, 58); return date;";

		Object result = JavaScriptDroid.evaluateScriptWithParameters(script, new String[0]);
		assertTrue(result instanceof GregorianCalendar);

		GregorianCalendar jsdate = (GregorianCalendar) result;
		GregorianCalendar javaDate = new GregorianCalendar(2012, 12, 31, 23, 59, 58);

		assertEquals(javaDate.get(Calendar.YEAR), jsdate.get(Calendar.YEAR));
		assertEquals(javaDate.get(Calendar.MONTH), jsdate.get(Calendar.MONTH));
		assertEquals(javaDate.get(Calendar.DAY_OF_MONTH), jsdate.get(Calendar.DAY_OF_MONTH));
		assertEquals(javaDate.get(Calendar.HOUR_OF_DAY), jsdate.get(Calendar.HOUR_OF_DAY));
		assertEquals(javaDate.get(Calendar.MINUTE), jsdate.get(Calendar.MINUTE));
		assertEquals(javaDate.get(Calendar.SECOND), jsdate.get(Calendar.SECOND));
	}

	@Test
	public void evaluateScriptReturningArray() throws JavaScriptException {
		String script = "[1, '2', true];";

		Object[] array = (Object[]) JavaScriptDroid.evaluateScript(script);

		assertNotNull(array);
		assertEquals(3, array.length);
		assertEquals(1d, array[0]);
		assertEquals("2", array[1]);
		assertEquals(true, array[2]);
	}

	@Test
	public void evaluateScriptWithParametersReturningArray() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject cls = new JSObject();
		String script = "return [1, '2', cls];";

		Object[] array = (Object[]) JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "cls" }, cls);

		assertNotNull(array);
		assertEquals(3, array.length);
		assertEquals(1d, array[0]);
		assertEquals("2", array[1]);
		assertEquals(JSObject.class, array[2].getClass());
	}

	@Test
	public void evaluateScriptWithParametersDate() throws JavaScriptException {
		GregorianCalendar javaDate = new GregorianCalendar(2012, 12, 31, 23, 59, 58);

		String script =  "return date.getFullYear() == " + javaDate.get(Calendar.YEAR)
				+ " && date.getMonth() 	   	 == " + javaDate.get(Calendar.MONTH)
				+ " && date.getDate()	   	 == " + javaDate.get(Calendar.DAY_OF_MONTH)
				+ " && date.getHours()		 == " + javaDate.get(Calendar.HOUR_OF_DAY)
				+ " && date.getMinutes() 	 == " + javaDate.get(Calendar.MINUTE)
				+ " && date.getSeconds()	 == " + javaDate.get(Calendar.SECOND)
				+ ";";

		Object result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "date" }, javaDate);

		assertTrue((Boolean) result);
	}

	@Test
	public void evaluateScriptWithParametersArray() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject cls = new JSObject();
		Object[] array = {5, "A", cls};

		String script = "return array[0] + array[1] + array[2];";

		Object result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "array" }, new Object[]{ array });

		assertEquals("5A[object JSObject]", result);
	}

	@Test
	public void evaluateScriptWithParametersUnmappedObject() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		Character obj = 'a';

		try {
			JavaScriptDroid.evaluateScriptWithParameters("", new String[] { "obj" }, obj);

			fail("A JavaScriptClassUnregistered should've been thrown");
		} catch (JavaScriptClassUnregistered ex) {
			assertEquals("Class Character not registered. Did you forget to call registerClass first?", ex.getMessage());
		}
	}

	@Test
	public void instanceOf() throws JavaScriptException {
        JavaScriptDroid.registerClass(JSObject.class);
		JavaScriptDroid.registerClass(JSObjectObjectConstructor.class);

		JSObject obj = new JSObjectObjectConstructor(new JSObject());

		String script = "return obj instanceof JSObjectObject;";
		Boolean result = (Boolean) JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "obj" }, obj);
		assertTrue(result);
		
		script = "return obj.objectValue instanceof JSObject;";
		result = (Boolean) JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "obj" }, obj);
		assertTrue(result);
		
		script = "return obj.getObject() instanceof JSObject;";
		result = (Boolean) JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "obj" }, obj);
		assertTrue(result);
	}

	@Test
	public void evaluateScriptWithSyntaxError() throws JavaScriptClassUnregistered {

		String script = "switche ( vin diesel );";

		// Testing evaluateScript
		try {
			JavaScriptDroid.evaluateScript(script);
			fail("A JavaScriptException should've been thrown");
		} catch (JavaScriptException ex) {
			assertEquals("SyntaxError: Unexpected identifier 'diesel'. Expected ')' to end an argument list.", ex.getMessage());
		}

		// Testing evaluateScriptWithParameters
		try {
			JavaScriptDroid.evaluateScriptWithParameters(script, new String[0]);
			fail("A JavaScriptException should've been thrown");
		} catch (JavaScriptException ex) {
			assertEquals("SyntaxError: Unexpected identifier 'diesel'. Expected ')' to end an argument list.", ex.getMessage());
		}
	}

	@Test
	public void evaluateScriptWithJavaScriptError() throws JavaScriptClassUnregistered {
		String script = "throw \"JavaScript Error\"";

		// Testing evaluateScript
		try {
			JavaScriptDroid.evaluateScript(script);
			fail("A JavaScript exception should be thrown.");
		} catch (JavaScriptException ex) {
			assertEquals("JavaScript Error", ex.getMessage());
		}

		// Testing evaluateScriptWithParameters
		try {
			JavaScriptDroid.evaluateScriptWithParameters(script, new String[] {"teste"}, "teste");
			fail("A JavaScript exception should be thrown.");
		} catch (JavaScriptException ex) {
			assertEquals("JavaScript Error", ex.getMessage());
		}
	}

	@Test
	public void objectConcatenationWithString()
			throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject cls = new JSObject();

		// Concatenation
		String script = "return cls + '';";
		Object result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "cls" }, cls);

		assertEquals("[object JSObject]", result);

		// toString
		script = "return cls.toString()";
		result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "cls" }, cls);

		assertEquals("[object JSObject]", result);

		// valueOf
		script = "return cls.valueOf()";
		result = JavaScriptDroid.evaluateScriptWithParameters(script,
				new String[] { "cls" }, cls);

		assertEquals("[object JSObject]", result);
	}

	@Test
	public void exceptionMapping() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		String script =
				"try { obj.throwException(); }" +
				"catch(e) { return e.message; }";
		Object result = JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "obj" }, new JSObject());
		assertNotNull(result);
		assertEquals("Exception Test", result);

		script =
				"try { throw new Error('Exception Test'); }" +
						"catch(e) { return e.message; }";
		result = JavaScriptDroid.evaluateScriptWithParameters(
				script, new String[] { "obj" }, new JSObject());
		assertNotNull(result);
		assertEquals("Exception Test", result);
	}

	@Test
	public void getJavascriptStackTraceFromError() {

		try {
			JavaScriptDroid.evaluateScript("" +
							  "/*1*/                                \n"
							+ "/*2*/                                \n"
							+ "/*3*/function f1() {                 \n"
							+ "/*4*/    throw new Error('Error');	\n"
							+ "/*5*/}                               \n"
							+ "/*6*/                                \n"
							+ "/*7*/function f2() {                 \n"
							+ "/*8*/    return f1();                \n"
							+ "/*9*/}                               \n"
							+ "/*10*/function f3() {                \n"
							+ "/*11*/    return f2();               \n"
							+ "/*12*/}                              \n",
					"Test.js");

			JavaScriptDroid.evaluateScript("f3();");
			fail("A JavaScriptException should've been thrown");

		} catch (JavaScriptException ex) {
			assertEquals("" +
					"f1@Test.js:4:25\n" +
					"f2@Test.js:8:19\n" +
					"f3@Test.js:11:20\n" +
					"global code", ex.getJavaScriptStackTrace());
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateScript(String script, JSObject obj, Class<T> returnClazz) throws JavaScriptException {
		if(obj != null) {
			JavaScriptDroid.evaluateScriptWithParameters("obj = _obj", new String[] { "_obj" }, obj);
		}
		return (T) JavaScriptDroid.evaluateScript(script);
	}

	@Test
	public void evaluateScriptReturningDouble() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.doubleValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.doubleValue, result.doubleValue(), 0);

		script = "obj['doubleValue'];";
		result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.doubleValue, result.doubleValue(), 0);
	}

	@Test
	public void evaluateScriptReturningBoolean() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.booleanValue;";
		Boolean result = evaluateScript(script, obj, Boolean.class);
		assertEquals(obj.booleanValue, result);
	}

	@Test
	public void evaluateScriptReturningInt() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.intValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.intValue, result.intValue());
	}

	@Test
	public void evaluateScriptReturningFloat() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.floatValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.floatValue, result.floatValue(), 0);
	}

	@Test
	public void evaluateScriptReturningByte() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.byteValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.byteValue, result.byteValue());
	}

	@Test
	public void evaluateScriptReturningShort() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.shortValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.shortValue, result.shortValue());
	}

	@Test
	public void evaluateScriptReturningLong() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.longValue;";
		Number result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.longValue, result.longValue());
	}

	@Test
	public void evaluateScriptReturningNumber() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.numberValue;";
		Object result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.numberValue, result);
	}

	@Test
	public void evaluateScriptReturningString() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.stringValue;";
		Object result = evaluateScript(script, obj, String.class);
		assertEquals(obj.stringValue, result);
	}

	@Test
	public void evaluateScriptReturningDateFromObject() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.dateValue;";
		Object result = evaluateScript(script, obj, Number.class);
		assertEquals(obj.dateValue, result);
	}

	@Test
	public void evaluateScriptReturningArrayFromObject() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.arrayValue;";
		Object[] result = evaluateScript(script, obj, Object[].class);
		assertNotNull(obj.getArray());
		assertEquals(obj.arrayValue.length, result.length);
		assertEquals(obj.arrayValue[0], ((Number)result[0]).doubleValue());
		assertEquals(obj.arrayValue[1], result[1]);
		assertEquals(obj.arrayValue[2], result[2]);
	}

	@Test
	public void evaluateScriptReturningNull() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();

		String script = "obj.objectValue;";
		Object result = evaluateScript(script, obj, Object.class);
		assertNull(result);
	}

	@Test
	public void evaluateScriptReturningObject() throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();
		obj.setObject(obj);

		String script = "obj.objectValue;";
		Object result = evaluateScript(script, obj, Object.class);
		assertEquals(obj.getObject(), result);
		assertSame(obj, result);
	}

	@Test
	public void evaluateScriptReturningJavaScriptObject() throws JavaScriptException {
		String script = "new Object()";
		Object result = evaluateScript(script, null, Void.class);
		assertEquals(UnmappedObject.class, result.getClass());
	}

	@Test
	public void evaluateScriptThrowingException() throws JavaScriptClassUnregistered {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();
		
		try {			
			String script = "obj.throwException();";
			evaluateScript(script, obj, Object.class);
			
			fail("A JavaScriptException should've been thrown.");
		} catch (JavaScriptException ex) {
			assertEquals("Exception Test", ex.getMessage());
		}
	}

	@Test
	public void evaluateScriptThrowingNullPointerException()
			throws JavaScriptException {
		JavaScriptDroid.registerClass(JSObject.class);

		JSObject obj = new JSObject();
		
		try {			
			String script = "obj.throwNullPointerException()";
			evaluateScript(script, obj, Object.class);
			
			fail("A NullPointerException should've been thrown");

		} catch (NullPointerException ex) {
			// Ok! Success...
		}
	}
}
