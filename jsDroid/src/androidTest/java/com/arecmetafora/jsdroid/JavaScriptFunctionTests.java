package com.arecmetafora.jsdroid;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class JavaScriptFunctionTests extends InstrumentationTestCase {

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

	private Object callScript(String script, JSObject obj)
			throws JavaScriptException {
		return JavaScriptDroid.evaluateScriptWithParameters(script, new String[] { "obj" }, obj);
	}

	@Test
	public void functionReturningDouble() throws JavaScriptException {
		JSObject obj = new JSObject();
		String script = "return obj.getDouble();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getDouble(), result.doubleValue());
	}

	@Test
	public void functionWithParameterDouble() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setDouble(1);";
		callScript(script, obj);
		assertEquals(1d, obj.getDouble());
	}

	@Test
	public void functionReturningBoolean() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getBoolean();";
		Boolean result = callScript(script, obj, Boolean.class);
		assertEquals(obj.getBoolean(), result.booleanValue());
	}

	@Test
	public void functionWithParameterBoolean() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setBoolean(false);";
		callScript(script, obj);
		assertFalse(obj.getBoolean());
	}

	@Test
	public void functionReturningInt() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getInt();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getInt(), result.intValue());
	}

	@Test
	public void functionWithParameterInt() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setInt(1);";
		callScript(script, obj);
		assertEquals(1, obj.getInt());
	}

	@Test
	public void functionReturningFloat() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getFloat();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getFloat(), result.floatValue());
	}

	@Test
	public void functionWithParameterFloat() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setFloat(1);";
		callScript(script, obj);
		assertEquals(1f, obj.getFloat());
	}

	@Test
	public void functionReturningByte() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getByte();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getByte(), result.byteValue());
	}

	@Test
	public void functionWithParameterByte() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setByte(1);";
		callScript(script, obj);
		assertEquals(1, obj.getByte());
	}

	@Test
	public void functionReturningShort() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getShort();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getShort(), result.shortValue());
	}

	@Test
	public void functionWithParameterShort() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setShort(1);";
		callScript(script, obj);
		assertEquals(1, obj.getShort());
	}

	@Test
	public void functionReturningLong() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getLong();";
		Number result = callScript(script, obj, Number.class);
		assertEquals(obj.getLong(), result.longValue());
	}

	@Test
	public void functionWithParameterLong() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setLong(1);";
		callScript(script, obj);
		assertEquals(1, obj.getLong());
	}

	@Test
	public void functionReturningNumber() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getNumber();";
		Object result = callScript(script, obj, Number.class);
		assertEquals(obj.getNumber(), result);
	}

	@Test
	public void functionWithParameterNumber() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setNumber(1);";
		callScript(script, obj);
		assertEquals(1, obj.getNumber().intValue());
	}

	@Test
	public void functionReturningString() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getString();";
		Object result = callScript(script, obj, String.class);
		assertEquals(obj.getString(), result);
	}

	@Test
	public void functionWithParameterString() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setString('STR');";
		callScript(script, obj);
		assertEquals("STR", obj.getString());
	}

	@Test
	public void functionReturningDate() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getDate();";
		Object result = callScript(script, obj, Number.class);
		assertEquals(obj.getDate(), result);
	}

	@Test
	public void functionWithParameterDate() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setDate(new Date(2012, 11, 31, 23, 59, 58));";
		callScript(script, obj);
		assertEquals(2012, obj.getDate().get(Calendar.YEAR));
		assertEquals(11, obj.getDate().get(Calendar.MONTH));
		assertEquals(31, obj.getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(23, obj.getDate().get(Calendar.HOUR_OF_DAY));
		assertEquals(59, obj.getDate().get(Calendar.MINUTE));
		assertEquals(58, obj.getDate().get(Calendar.SECOND));
	}

	@Test
	public void functionReturningArray() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getArray();";
		Object[] result = callScript(script, obj, Object[].class);
		assertNotNull(obj.getArray());
		assertEquals(obj.getArray().length, result.length);
		assertEquals(obj.getArray()[0], ((Number) result[0]).doubleValue());
		assertEquals(obj.getArray()[1], result[1]);
		assertEquals(obj.getArray()[2], result[2]);
	}

	@Test
	public void functionWithParameterArray() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setArray([false, 2, '3']);";
		callScript(script, obj);
		assertNotNull(obj.getArray());
		assertEquals(3, obj.getArray().length);
		assertFalse((Boolean) obj.getArray()[0]);
		assertEquals(2.0, ((Number) obj.getArray()[1]).doubleValue());
		assertEquals("3", (String) obj.getArray()[2]);
	}

	@Test
	public void functionReturningNull() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "return obj.getObject();";
		Object result = callScript(script, obj, Object.class);
		assertNull(result);
	}

	@Test
	public void functionWithParameterNull() throws JavaScriptException {
		JSObject obj = new JSObject();
		obj.setObject(obj);

		String script = "obj.setNullableObject(null);";
		callScript(script, obj);
		assertNull(obj.getObject());
	}

	@Test
	public void functionReturningObject() throws JavaScriptException {
		JSObject obj = new JSObject();
		obj.setObject(obj);

		String script = "return obj.getObject();";
		Object result = callScript(script, obj, Object.class);
		assertEquals(obj.getObject(), result);
		assertSame(obj, result);
	}

	@Test
	public void functionWithParameterObject() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.setObject(obj);";
		callScript(script, obj);
		assertSame(obj, obj.getObject());
	}

	@Test
	public void functionNotMapped() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.notMappedFunction = function() { return 'NotMappedFunctionReturn'; }; " +
						"return obj.notMappedFunction();";
		Object result = callScript(script, obj);
		assertEquals("NotMappedFunctionReturn", result);
	}

	@Test
	public void functionThrowingException() {
		JSObject obj = new JSObject();

		try {
			String script = "obj.throwException();";
			callScript(script, obj);

			fail("A Exception should've been thrown.");
		} catch (Exception ex) {
			assertEquals("Exception Test", ex.getMessage());
		}
	}

	@Test
	public void functionThrowingNullPointerException()
			throws JavaScriptException {
		JSObject obj = new JSObject();

		try {
			String script = "obj.throwNullPointerException();";
			callScript(script, obj);

			fail("A NullPointerException should've been thrown");

		} catch (NullPointerException ex) {
			// Ok! Success...
		}
	}

	@Test
	public void functionReturningUnmappedObject() throws JavaScriptException {
		JSObject obj = new JSObject();
		try {			
			String script = "obj.getUnmappedObject();";
			callScript(script, obj);
			
			fail("A JavaScriptException should've been thrown");
		} catch (JavaScriptClassUnregistered ex) {
			assertEquals("Class Date not registered. Did you forget to call registerClass first?", ex.getMessage());
		}
	}

	private void callScriptAndValidateCastException(String method,
			JSObject obj, Object value, Class<?> expectedType) throws JavaScriptException {
		try {
			String script = "obj." + method + "(value);";
			JavaScriptDroid.evaluateScriptWithParameters(script, new String[] {
					"obj", "value" }, obj, value);

			fail("A JavaScriptMethodParamTypeInvalid should've been thrown.");
		} catch (JavaScriptMethodParamTypeInvalid ex) {
			assertEquals(String.format(
					"Parameter '%s' of method '%s' from '%s' is not instance of '%s'", "arg0",
					method, obj.getClass().getSimpleName(), expectedType.getSimpleName()),
					ex.getMessage());
		}
	}

	@Test
	public void functionParameterWithInvalidType() throws JavaScriptException {

		JSObject obj = new JSObject();
		Object[] paramsToTest = new Object[] { "STR", 1d, true,
				new GregorianCalendar(), new Object[] {}, new JSObject() };

		for (Object param : paramsToTest) {

			if (!(param instanceof Number)) {
				callScriptAndValidateCastException("setDouble", obj, param, double.class);
				callScriptAndValidateCastException("setInt", obj, param, int.class);
				callScriptAndValidateCastException("setFloat", obj, param, float.class);
				callScriptAndValidateCastException("setByte", obj, param, byte.class);
				callScriptAndValidateCastException("setShort", obj, param, short.class);
				callScriptAndValidateCastException("setLong", obj, param, long.class);
				callScriptAndValidateCastException("setNumber", obj, param, Number.class);
			}

			if (!(param instanceof Boolean)) {
				callScriptAndValidateCastException("setBoolean", obj, param, boolean.class);
			}

			if (!(param instanceof String)) {
				callScriptAndValidateCastException("setString", obj, param, String.class);
			}

			if (!(param instanceof GregorianCalendar)) {
				callScriptAndValidateCastException("setDate", obj, param, GregorianCalendar.class);
			}

			if (!(param instanceof Object[])) {
				callScriptAndValidateCastException("setArray", obj, param, Object[].class);
			}

			if (!(param instanceof JSObject)) {
				callScriptAndValidateCastException("setObject", obj, param, JSObject.class);
			}
		}

        // Invalid Date
        try {
            String script = "obj.setDate(new Date(\"invalidDate\"));";
            callScript(script, obj);

            fail("A JavaScriptMethodParamTypeInvalid should've been thrown.");
        } catch (JavaScriptMethodParamTypeInvalid ex) {
            assertEquals(String.format(
                    "Parameter '%s' of method '%s' from '%s' is not instance of '%s'", "arg0",
                    "setDate", obj.getClass().getSimpleName(), GregorianCalendar.class.getSimpleName()),
                    ex.getMessage());
        }
	}

	private void callScriptAndValidateRequiredException(String method,
			JSObject obj) throws JavaScriptException {
		try {
			String script = "obj." + method + "();";
			JavaScriptDroid.evaluateScriptWithParameters(script,
					new String[] { "obj" }, obj);

			fail("An JavaScriptMethodParamRequired should've been thrown.");
		} catch (JavaScriptMethodParamRequired ex) {
			assertEquals(String.format(
					"Parameter '%s' of method '%s' from '%s' does not accept null",
					"arg0", method, obj.getClass().getSimpleName()),
					ex.getMessage());
		}
	}

	@Test
	public void functionParameterWithNullValue() throws JavaScriptException {
		JSObject obj = new JSObject();

		callScriptAndValidateRequiredException("setDouble", obj);
		callScriptAndValidateRequiredException("setBoolean", obj);
		callScriptAndValidateRequiredException("setInt", obj);
		callScriptAndValidateRequiredException("setFloat", obj);
		callScriptAndValidateRequiredException("setByte", obj);
		callScriptAndValidateRequiredException("setShort", obj);
		callScriptAndValidateRequiredException("setLong", obj);
		callScriptAndValidateRequiredException("setObject", obj);
		callScriptAndValidateRequiredException("setString", obj);
		callScriptAndValidateRequiredException("setDate", obj);
		callScriptAndValidateRequiredException("setArray", obj);
		callScriptAndValidateRequiredException("setNumber", obj);
	}

	@Test
	public void getJavaScriptStackTraceFromFunction() throws JavaScriptClassUnregistered {
		try {
			JavaScriptDroid.evaluateScript("" +
					  "/*1 */                      		\n"
					+ "/*2 */                      		\n"
					+ "/*3 */function f1() {       		\n"
					+ "/*4 */    o.throwException();  	\n"
					+ "/*5 */}                     		\n"
					+ "/*6 */                      		\n"
					+ "/*7 */function f2() {       		\n"
					+ "/*8 */    return f1();      		\n"
					+ "/*9 */}                     		\n"
					+ "/*10*/function f3() {       		\n"
					+ "/*11*/    return f2();      		\n"
					+ "/*12*/}                     		\n", "Test.js");

			callScript("o = obj", new JSObject());
			JavaScriptDroid.evaluateScript("f3();");
			fail("A JavaScriptException should've been thrown");

		} catch (JavaScriptException ex) {
			assertEquals("" +
					"#0 throwException() at [native code]\n" +
					"#1 f1() at Test.js:4\n" +
					"#2 f2() at Test.js:8\n" +
					"#3 f3() at Test.js:11\n" +
					"#4 global code() at :1", ex.getJavaScriptStackTrace());
		}
	}

	// These Stress tests is a not a performance tests. It guarantees that
	// all JNI references are being released properly
	@Test
	public void functionStress() throws JavaScriptException {

		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   obj.withParams(1.0, false, 'STR', new Date(), [false, 1, '2', new Date(), obj], obj);"
				+ "}";

		callScript(script, new JSObject());
	}

	//@Test
	public void functionThrowingExceptionStress()
			throws JavaScriptException {

		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   try { obj.throwException(); } catch (e) { }"
				+ "}";

		callScript(script, new JSObject());
	}
}
