package com.arecmetafora.jsdroid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to denotate when a parameter/property does not accept null values
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNull {
}
