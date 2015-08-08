package org.squiddev.petit.annotation;

import org.squiddev.petit.api.tree.ArgumentKind;

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
@Retention(RetentionPolicy.SOURCE)
public @interface Provided {
}
