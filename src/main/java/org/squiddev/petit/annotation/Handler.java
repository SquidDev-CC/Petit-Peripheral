package org.squiddev.petit.annotation;

/**
 * Handles a method on an interface
 */
public @interface Handler {
	/**
	 * The interface we handle the method on
	 *
	 * @return The method's interface
	 */
	Class<?> value();
}
