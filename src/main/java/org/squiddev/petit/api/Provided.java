package org.squiddev.petit.api;

import org.squiddev.petit.api.compile.ArgumentKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a provided argument.
 *
 * @see ArgumentKind#PROVIDED
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provided {
}
