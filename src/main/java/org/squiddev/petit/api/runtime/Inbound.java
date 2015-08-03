package org.squiddev.petit.api.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method that validates and converts a type.
 *
 * This must take a value and an argument name. If can throw or return null
 * to fail.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Inbound {
	/**
	 * Get the friendly name for this method
	 *
	 * @return The default name
	 */
	String value() default "";

	/**
	 * Get the backends this supports.
	 *
	 * @return The backends supported
	 */
	Class<?>[] backends() default {};
}
