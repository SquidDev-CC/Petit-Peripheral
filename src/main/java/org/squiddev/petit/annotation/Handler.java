package org.squiddev.petit.annotation;

import java.lang.annotation.*;

/**
 * Handles a method on an interface
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Handler {
	/**
	 * The interface we handle the method on
	 *
	 * @return The method's interface
	 */
	Class<?> value();
}
