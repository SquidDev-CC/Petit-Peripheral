package org.squiddev.petit.api.tree;

/**
 * Validate an object
 *
 * @param <T> The type of object to verify
 */
public interface Verifier<T> {
	/**
	 * Verify the object
	 *
	 * @param object The object to verify
	 * @return If the object is valid
	 */
	boolean verify(T object);
}
