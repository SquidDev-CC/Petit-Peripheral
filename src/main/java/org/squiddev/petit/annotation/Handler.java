package org.squiddev.petit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Handles a method on an interface
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Handler {
	/**
	 * The interface we handle the method on
	 *
	 * @return The method's interface
	 */
	Class<?> value();
}
