package org.squiddev.petit.annotation.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method that converts from a specified type to Lua.
 *
 * This must take a value and return its value.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Outbound {
	/**
	 * Get the backends this supports.
	 *
	 * @return The backends supported
	 */
	Class<?>[] backends() default {};
}
