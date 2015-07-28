package org.squiddev.petit.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an optional parameter.
 *
 * This must not be a primitive as {@code null} will be used,
 * you can always use the boxed equivalent instead.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
