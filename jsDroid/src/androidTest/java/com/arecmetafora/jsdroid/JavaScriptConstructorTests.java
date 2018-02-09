package com.arecmetafora.jsdroid;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JavaScriptConstructorTests extends InstrumentationTestCase {

    @Before
    public void registerClass() {
        JavaScriptDroid.registerClass(JSObject.class);
        JavaScriptDroid.registerClass(JSObjectDoubleConstructor.class);
        JavaScriptDroid.registerClass(JSObjectBooleanConstructor.class);
        JavaScriptDroid.registerClass(JSObjectIntConstructor.class);
        JavaScriptDroid.registerClass(JSObjectFloatConstructor.class);
        JavaScriptDroid.registerClass(JSObjectByteConstructor.class);
        JavaScriptDroid.registerClass(JSObjectShortConstructor.class);
        JavaScriptDroid.registerClass(JSObjectLongConstructor.class);
        JavaScriptDroid.registerClass(JSObjectObjectConstructor.class);
		JavaScriptDroid.registerClass(JSObjectNullableObject.class);
        JavaScriptDroid.registerClass(JSObjectStringConstructor.class);
        JavaScriptDroid.registerClass(JSObjectDateConstructor.class);
        JavaScriptDroid.registerClass(JSObjectArrayConstructor.class);
        JavaScriptDroid.registerClass(JSObjectNumberConstructor.class);
        JavaScriptDroid.registerClass(JSObjectUnmappedConstructor.class);
        JavaScriptDroid.registerClass(JSObjectExceptionConstructor.class);
        JavaScriptDroid.registerClass(JSObjectNullPointerConstructor.class);
        JavaScriptDroid.registerClass(JSObjectConstructorWithParams.class);
    }

	private JSObject callScript(String script) throws JavaScriptException {
        return (JSObject) JavaScriptDroid.evaluateScriptWithParameters(script,
                new String[0]);
	}

	@Test
	public void constructorWithNoParameters() throws JavaScriptException {
		String script = "return new JSObject();";
		JSObject obj = callScript(script);
		assertNotNull(obj);
		assertEquals(JSObject.class, obj.getClass());
	}

    @Test
	public void constructorWithParameterDouble() throws JavaScriptException {
		String script = "return new JSObjectDouble(1);";
		JSObject obj = callScript(script);
		assertEquals(1d, obj.getDouble());
	}

    @Test
	public void constructorWithParameterBoolean() throws JavaScriptException {
		String script = "return new JSObjectBoolean(false);";
		JSObject obj = callScript(script);
		assertFalse(obj.getBoolean());
	}

    @Test
	public void constructorWithParameterInt() throws JavaScriptException {
		String script = "return new JSObjectInt(1);";
		JSObject obj = callScript(script);
		assertEquals(1, obj.getInt());
	}

    @Test
	public void constructorWithParameterFloat() throws JavaScriptException {
		String script = "return new JSObjectFloat(1);";
		JSObject obj = callScript(script);
		assertEquals(1f, obj.getFloat());
	}

    @Test
	public void constructorWithParameterByte() throws JavaScriptException {
		String script = "return new JSObjectByte(1);";
		JSObject obj = callScript(script);
		assertEquals(1, obj.getByte());
	}

    @Test
	public void constructorWithParameterShort() throws JavaScriptException {
		String script = "return new JSObjectShort(1);";
		JSObject obj = callScript(script);
		assertEquals(1, obj.getShort());
	}

    @Test
	public void constructorWithParameterLong() throws JavaScriptException {
		String script = "return new JSObjectLong(1);";
		JSObject obj = callScript(script);
		assertEquals(1, obj.getLong());
	}

    @Test
	public void constructorWithParameterNumber() throws JavaScriptException {
		String script = "return new JSObjectNumber(1);";
		JSObject obj = callScript(script);
		assertEquals(1, obj.getNumber().intValue());
	}

    @Test
	public void constructorWithParameterString() throws JavaScriptException {
		String script = "return new JSObjectString('STR');";
		JSObject obj = callScript(script);
		assertEquals("STR", obj.getString());
	}

    @Test
	public void constructorWithParameterDate() throws JavaScriptException {
		String script = "return new JSObjectDate(new Date(2012, 11, 31, 23, 59, 58));";
		JSObject obj = callScript(script);
		assertEquals(2012, obj.getDate().get(Calendar.YEAR));
		assertEquals(11, obj.getDate().get(Calendar.MONTH));
		assertEquals(31, obj.getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(23, obj.getDate().get(Calendar.HOUR_OF_DAY));
		assertEquals(59, obj.getDate().get(Calendar.MINUTE));
		assertEquals(58, obj.getDate().get(Calendar.SECOND));
	}

    @Test
	public void constructorWithParameterArray() throws JavaScriptException {
		String script = "return new JSObjectArray([false, 2, '3']);";
		JSObject obj = callScript(script);
		assertNotNull(obj.getArray());
		assertEquals(3, obj.getArray().length);
		assertFalse((Boolean) obj.getArray()[0]);
		assertEquals(2.0, ((Number) obj.getArray()[1]).doubleValue());
		assertEquals("3", (String) obj.getArray()[2]);
	}

    @Test
	public void constructorWithParameterNull() throws JavaScriptException {
		String script = "return new JSObjectNullableObject();";
		JSObject obj = callScript(script);
		assertNull(obj.getObject());
	}

    @Test
	public void constructorWithParameterObject() throws JavaScriptException {
		String script = "return new JSObjectObject(new JSObject());";
		JSObject obj = callScript(script);
		assertNotNull(obj);
		assertNotNull(obj.getObject());
		assertEquals(JSObjectObjectConstructor.class, obj.getClass());
	}

    @Test
	public void constructorThrowingException() {

		try {
			String script = "return new JSObjectException();";
			callScript(script);

			fail("An Exception should've been thrown.");
		} catch (Exception ex) {
			assertEquals("Exception Test", ex.getMessage());
		}
	}

	@Test
	public void constructorNotMapped() throws JavaScriptException {

		String script = "return new JSObjectUnmapped();";
		JSObject result = callScript(script);
		assertNotNull(result);
		assertEquals(JSObjectUnmappedConstructor.class, result.getClass());
	}

    @Test
	public void constructorThrowingNullPointerException()
			throws JavaScriptException {

		try {
			String script = "return new JSObjectNullPointer();";
			callScript(script);

			fail("A NullPointerException should've been thrown");

		} catch (NullPointerException ex) {
			// Ok! Success...
		}
	}

	private void callScriptAndValidateCastException(String cls, Object value, Class<?> expectedType)
			throws JavaScriptException {
		try {
			String script = "return new " + cls + "(value);";
			JavaScriptDroid.evaluateScriptWithParameters(script,
					new String[] { "value" }, value);

            fail("A JavaScriptMethodParamTypeInvalid should've been thrown.");
        } catch (JavaScriptMethodParamTypeInvalid ex) {
            assertEquals(String.format(
                    "Parameter '%s' of method '%s' from '%s' is not instance of '%s'", "arg0",
                    "<constructor>", cls, expectedType.getSimpleName()), ex.getMessage());
        }
	}

    @Test
	public void constructorParameterWithInvalidType() throws JavaScriptException {

		Object[] paramsToTest = new Object[] { "STR", 1d, true,
				new GregorianCalendar(), new Object[] {}, new JSObject() };

		for (Object param : paramsToTest) {

			if (!(param instanceof Number)) {
				callScriptAndValidateCastException("JSObjectDouble", param, double.class);
				callScriptAndValidateCastException("JSObjectInt", param, int.class);
				callScriptAndValidateCastException("JSObjectFloat", param, float.class);
				callScriptAndValidateCastException("JSObjectByte", param, byte.class);
				callScriptAndValidateCastException("JSObjectShort", param, short.class);
				callScriptAndValidateCastException("JSObjectLong", param, long.class);
				callScriptAndValidateCastException("JSObjectNumber", param, Number.class);
			}

			if (!(param instanceof Boolean)) {
				callScriptAndValidateCastException("JSObjectBoolean", param, boolean.class);
			}

			if (!(param instanceof String)) {
				callScriptAndValidateCastException("JSObjectString", param, String.class);
			}

			if (!(param instanceof GregorianCalendar)) {
				callScriptAndValidateCastException("JSObjectDate", param, GregorianCalendar.class);
			}

			if (!(param instanceof Object[])) {
				callScriptAndValidateCastException("JSObjectArray", param, Object[].class);
			}

			if (!(param instanceof JSObject)) {
				callScriptAndValidateCastException("JSObjectObject", param, JSObject.class);
			}
		}

		// Invalid Date
		try {
			String script = "new JSObjectDate(new Date(\"invalidDate\"));";
			callScript(script);

			fail("A JavaScriptMethodParamTypeInvalid should've been thrown.");
		} catch (JavaScriptMethodParamTypeInvalid ex) {
			assertEquals(String.format(
					"Parameter '%s' of method '%s' from '%s' is not instance of '%s'", "arg0",
					"<constructor>", "JSObjectDate", GregorianCalendar.class.getSimpleName()),
					ex.getMessage());
		}
	}

	private void callScriptAndValidateRequiredException(String cls) throws JavaScriptException {
		try {
			String script = "new " + cls + "();";
			JavaScriptDroid.evaluateScript(script);

            fail("An JavaScriptMethodParamRequired should've been thrown.");
        } catch (JavaScriptMethodParamRequired ex) {
            assertEquals(String.format(
                    "Parameter '%s' of method '%s' from '%s' does not accept null",
                    "arg0", "<constructor>", cls), ex.getMessage());
        }
	}

    @Test
	public void constructorParameterWithNullValue() throws JavaScriptException {
        callScriptAndValidateRequiredException("JSObjectDouble");
        callScriptAndValidateRequiredException("JSObjectBoolean");
        callScriptAndValidateRequiredException("JSObjectInt");
        callScriptAndValidateRequiredException("JSObjectFloat");
        callScriptAndValidateRequiredException("JSObjectByte");
        callScriptAndValidateRequiredException("JSObjectShort");
        callScriptAndValidateRequiredException("JSObjectLong");
        callScriptAndValidateRequiredException("JSObjectObject");
        callScriptAndValidateRequiredException("JSObjectString");
        callScriptAndValidateRequiredException("JSObjectDate");
        callScriptAndValidateRequiredException("JSObjectArray");
        callScriptAndValidateRequiredException("JSObjectNumber");
	}

    @Test
	public void getJavaScriptStackTraceFromConstructor() {

		try {
			JavaScriptDroid.evaluateScript("" +
					  "/*1 */                      		\n"
					+ "/*2 */                      		\n"
					+ "/*3 */function f1() {       		\n"
					+ "/*4 */    new JSObjectException();\n"
					+ "/*5 */}                     		\n"
					+ "/*6 */                      		\n"
					+ "/*7 */function f2() {       		\n"
					+ "/*8 */    return f1();      		\n"
					+ "/*9 */}                     		\n"
					+ "/*10*/function f3() {       		\n"
					+ "/*11*/    return f2();      		\n"
					+ "/*12*/}                     		\n", "Test.js");

            JavaScriptDroid.evaluateScript("f3();");
			fail("A JavaScriptException should've been thrown");

        } catch (JavaScriptException ex) {
            assertEquals("" +
					"#0 () at [native code]\n" +
					"#1 f1() at Test.js:4\n" +
					"#2 f2() at Test.js:8\n" +
					"#3 f3() at Test.js:11\n" +
					"#4 global code() at :1", ex.getJavaScriptStackTrace());
		}
	}

	// These Stress tests is a not a performance tests. It guarantees that
	// all JNI references are being released properly
    @Test
	public void constructorStress() throws JavaScriptException {

		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   new JSObject(1.0, false, 'STR', new Date(), "
				+ "					[false, 1, '2', new Date(), new JSObject()], new JSObject());"
				+ "}";

		callScript(script);
	}

    //@Test
	public void constructorThrowingExceptionStress()
			throws JavaScriptException {

		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   try { new JSObject(); } catch (e) { }"
				+ "}";

		callScript(script);
	}
}