package com.arecmetafora.jsdroid;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JavaScriptSetPropertyTests extends InstrumentationTestCase {

    @Before
    public void registerClass() {
        JavaScriptDroid.registerClass(JSObject.class);
    }

	private Object callScript(String script, JSObject obj)
			throws JavaScriptException {
		return JavaScriptDroid.evaluateScriptWithParameters(script, new String[] { "obj" }, obj);
	}

	@Test
	public void setPropertyDouble() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.doubleValue = 1;";
		callScript(script, obj);
		assertEquals(1d, obj.getDouble());

		script = "obj['doubleValue'] = 1;";
		callScript(script, obj);
		assertEquals(1d, obj.getDouble());
	}

    @Test
	public void setPropertyBoolean() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.booleanValue = false;";
		callScript(script, obj);
		assertFalse(obj.getBoolean());
	}

    @Test
	public void setPropertyInt() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.intValue = 1;";
		callScript(script, obj);
		assertEquals(1, obj.getInt());
	}

    @Test
	public void setPropertyFloat() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.floatValue = 1;";
		callScript(script, obj);
		assertEquals(1f, obj.getFloat());
	}

    @Test
	public void setPropertyByte() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.byteValue = 1;";
		callScript(script, obj);
		assertEquals(1, obj.getByte());
	}

    @Test
	public void setPropertyShort() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.shortValue = 1;";
		callScript(script, obj);
		assertEquals(1, obj.getShort());
	}

    @Test
	public void setPropertyLong() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.longValue = 1;";
		callScript(script, obj);
		assertEquals(1, obj.getLong());
	}

    @Test
	public void setPropertyNumber() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.numberValue = 1;";
		callScript(script, obj);
		assertEquals(1, obj.getNumber().intValue());
	}

    @Test
	public void setPropertyString() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.stringValue = 'SW';";
		callScript(script, obj);
		assertEquals("SW", obj.getString());
	}

    @Test
	public void setPropertyDate() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.dateValue = new Date(2012, 11, 31, 23, 59, 58);";
		callScript(script, obj);
		assertEquals(2012, obj.getDate().get(Calendar.YEAR));
		assertEquals(11, obj.getDate().get(Calendar.MONTH));
		assertEquals(31, obj.getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(23, obj.getDate().get(Calendar.HOUR_OF_DAY));
		assertEquals(59, obj.getDate().get(Calendar.MINUTE));
		assertEquals(58, obj.getDate().get(Calendar.SECOND));
	}

    @Test
	public void setPropertyArray() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.arrayValue = [false, 2, '3'];";
		callScript(script, obj);
		assertNotNull(obj.getArray());
		assertEquals(3, obj.getArray().length);
		assertFalse((Boolean) obj.getArray()[0]);
		assertEquals(2.0, ((Number) obj.getArray()[1]).doubleValue());
		assertEquals("3", (String) obj.getArray()[2]);
	}

    @Test
	public void setPropertyNull() throws JavaScriptException {
		JSObject obj = new JSObject();
		obj.setNullableObject(obj);

		String script = "obj.nullableObject = null;";
		callScript(script, obj);
		assertNull(obj.nullableObject);
	}

    @Test
	public void setPropertyObject() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.objectValue = obj;";
		callScript(script, obj);
		assertSame(obj, obj.getObject());
	}

	@Test
	public void setPropertyNotMapped() throws JavaScriptException {
		JSObject obj = new JSObject();

		String script = "obj.notMappedProperty = 'CustomProperty'; return obj.notMappedProperty;";
		Object result = callScript(script, obj);
		assertEquals("CustomProperty", result);
	}

	private void callScriptAndValidateCastException(String property,
			JSObject obj, Object value, Class<?> expectedType)
			throws JavaScriptException {
		try {
			String script = "obj." + property + " = value;";
			JavaScriptDroid.evaluateScriptWithParameters(script, new String[] {
					"obj", "value" }, obj, value);

            fail("A JavaScriptPropertyTypeInvalid should've been thrown.");
        } catch (JavaScriptPropertyTypeInvalid ex) {
            assertEquals(String.format(
                    "Property '%s' of '%s' is not instance of '%s'",
                    property, obj.getClass().getSimpleName(), expectedType.getSimpleName()),
                    ex.getMessage());
        }
	}

    @Test
	public void setPropertyInvalidType() throws JavaScriptException {

		JSObject obj = new JSObject();
		Object[] paramsToTest = new Object[] { "SW", 1d, true,
				new GregorianCalendar(), new Object[] {}, new JSObject() };

		for (Object param : paramsToTest) {

			if (!(param instanceof Number)) {
				callScriptAndValidateCastException("doubleValue", obj, param, double.class);
				callScriptAndValidateCastException("intValue", obj, param, int.class);
				callScriptAndValidateCastException("floatValue", obj, param, float.class);
				callScriptAndValidateCastException("byteValue", obj, param, byte.class);
				callScriptAndValidateCastException("shortValue", obj, param, short.class);
				callScriptAndValidateCastException("longValue", obj, param, long.class);
				callScriptAndValidateCastException("numberValue", obj, param, Number.class);
			}

			if (!(param instanceof Boolean)) {
				callScriptAndValidateCastException("booleanValue", obj, param, boolean.class);
			}

			if (!(param instanceof String)) {
				callScriptAndValidateCastException("stringValue", obj, param, String.class);
			}

			if (!(param instanceof GregorianCalendar)) {
				callScriptAndValidateCastException("dateValue", obj, param, GregorianCalendar.class);
			}

			if (!(param instanceof Object[])) {
				callScriptAndValidateCastException("arrayValue", obj, param, Object[].class);
			}

			if (!(param instanceof JSObject)) {
				callScriptAndValidateCastException("objectValue", obj, param, JSObject.class);
			}
		}

		// Invalid Date
		try {
			String script = "obj.dateValue = new Date(\"invalidDate\");";
			callScript(script, obj);

			fail("A JavaScriptPropertyTypeInvalid should've been thrown.");
		} catch (JavaScriptPropertyTypeInvalid ex) {
			assertEquals(String.format(
					"Property '%s' of '%s' is not instance of '%s'",
					"dateValue", obj.getClass().getSimpleName(), GregorianCalendar.class.getSimpleName()),
					ex.getMessage());
		}
	}

	private void callScriptAndValidateRequiredException(String property,
			JSObject obj) throws JavaScriptException {
		try {
			String script = "obj." + property + " = null;";
			JavaScriptDroid.evaluateScriptWithParameters(script,
					new String[] { "obj" }, obj);

            fail("An JavaScriptPropertyRequired should've been thrown.");
        } catch (JavaScriptPropertyRequired ex) {
            assertEquals(String.format(
                    "Property '%s' of '%s' does not accept null",
                    property, obj.getClass().getSimpleName()),
                    ex.getMessage());
        }
	}

    @Test
	public void setPropertyWithNullValue() throws JavaScriptException {
		JSObject obj = new JSObject();

		callScriptAndValidateRequiredException("doubleValue", obj);
		callScriptAndValidateRequiredException("intValue", obj);
		callScriptAndValidateRequiredException("floatValue", obj);
		callScriptAndValidateRequiredException("byteValue", obj);
		callScriptAndValidateRequiredException("shortValue", obj);
		callScriptAndValidateRequiredException("longValue", obj);
		callScriptAndValidateRequiredException("booleanValue", obj);
		callScriptAndValidateRequiredException("objectValue", obj);
		callScriptAndValidateRequiredException("stringValue", obj);
		callScriptAndValidateRequiredException("dateValue", obj);
		callScriptAndValidateRequiredException("arrayValue", obj);
		callScriptAndValidateRequiredException("numberValue", obj);
	}

	// These Stress tests is a not a performance tests. It guarantees that
	// all JNI references are being released properly
    @Test
	public void setPropertyStress() throws JavaScriptException {
		JSObject obj = new JSObject();
		
		String script = 
				  "for (index = 0; index < 10000; index++) { "
				+ "	   obj.numberValue = 1.0;"
				+"	   obj.stringValue = 'SW';"
				+ "    obj.dateValue = new Date();"
				+ "    obj.arrayValue = [false, 1, '2', new Date(), obj];"
				+ "	   obj.objectValue = obj;"
				+ "}";

		callScript(script, obj);
	}
}
