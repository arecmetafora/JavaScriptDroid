package com.arecmetafora.jsdroid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to ease the mapping of Java Objects to JavaScript.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaScriptMapped {
    String name() default "";
}
