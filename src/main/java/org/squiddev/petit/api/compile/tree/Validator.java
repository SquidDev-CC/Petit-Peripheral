package org.squiddev.petit.api.compile.tree;

/**
 * Validate an object
 *
 * @param <T> The type of object to validate
 */
public interface Validator<T> {
	/**
	 * Validate the object
	 *
	 * @param object The object to validate
	 * @return If the object is valid
	 */
	boolean validate(T object);
}
