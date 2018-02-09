package com.arecmetafora.jsdroid;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class Utils {

    /**
     * Defines the regular expression to evaluate if a String is a DateTime type.
     */
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Checks if an object instance is equal from another object.
     *
     * @param value1 The first object to be compared.
     * @param value2 The second object to be compared.
     * @return TRUE if the objects are equal. FALSE otherwise.
     */
    public static boolean isEqual(Object value1, Object value2) {
        if (value1 != value2 && (value1 == null || value2 == null)) {
            return false;
        } else if (value1 != null && value1 instanceof java.lang.Double) {
            return value1.equals(Double.valueOf(value2.toString()));
        } else if (value2 != null && value2 instanceof java.lang.Double) {
            return value2.equals(Double.valueOf(value1.toString()));
        } else {
            return !Utils.isNotEquals(value1, value2);
        }
    }

    /**
     * Checks if an object instance is different from another object.
     *
     * @param value1 The first object to be compared.
     * @param value2 The second object to be compared.
     * @return TRUE if the objects are different. FALSE otherwise.
     */
    public static boolean isNotEquals(Object value1, Object value2) {
        return ((value1 == null && value2 != null)
                || (value1 != null && value2 == null) || (value1 != null
                && value2 != null && !value1.equals(value2)));
    }

    /**
     * Returns a default value if the object passed is {@code null}.
     *
     * <pre>
     * Utils.defaultIfNull(null, null) = null
     * Utils.defaultIfNull(null, "") = ""
     * Utils.defaultIfNull(null, "zz") = "zz"
     * Utils.defaultIfNull("abc", "x") = "abc"
     * Utils.defaultIfNull(Boolean.TRUE, Boolean.FALSE) = Boolean.TRUE
     * </pre>
     *
     * @param <T> the type of the object
     * @param object the {@code Object} to test, may be {@code null}
     * @param defaultValue the default value to return, may be {@code null}
     * @return {@code object} if it is not {@code null}, defaultValue otherwise
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return (object != null ? object : defaultValue);
    }

    /**
     * Check if the value is null or empty.
     *
     * @param value Value to be checked.
     * @return TRUE if the value is null or has an empty value. FALSE otherwise.
     */
    public static boolean isNullOrEmpty(Object value) {
        return value == null || value.toString().isEmpty();
    }

    /**
     * Converts the value to a date time string in a specific locale
     *
     * @param value The value to be converted.
     * @return The value as a date string.
     */
    public static String convertToDateTimeString(GregorianCalendar value) {
        Date date = value.getTime();

        return new SimpleDateFormat(Utils.DATETIME_PATTERN, Locale.ENGLISH)
                .format(date);
    }

    /**
     * Logs a message in the Logcat.
     * @param message The message to be logged.
     */
    public static void log(String message) {
        Log.i("JavaScriptDroid", message);
    }

    /**
     * Logs an exception in the Logcat.
     * @param ex The exception to be logged.
     */
    public static void log(Exception ex) {
        Log.e("JavaScriptDroid", ex.getMessage(), ex);
    }
}
