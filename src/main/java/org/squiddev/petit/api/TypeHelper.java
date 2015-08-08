package org.squiddev.petit.api;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Various helpers for working with types and type mirrors
 */
public interface TypeHelper {
	/**
	 * Gets the class for a type mirror
	 *
	 * @param mirror The mirror to get the class for
	 * @return The relevent class
	 * @throws ClassNotFoundException If the class cannot be found.
	 */
	Class<?> getType(TypeMirror mirror) throws ClassNotFoundException;

	/**
	 * Get the type mirror for a class
	 *
	 * @param type The class to get it for
	 * @return The converted type mirror.
	 */
	TypeMirror getMirror(Class<?> type);

	/**
	 * Get a type mirror for the {@link Object} type.
	 *
	 * @return The type mirror for {@link Object}
	 */
	TypeMirror object();

	/**
	 * Get a type mirror for an array of {@link Object}s.
	 *
	 * @return An array type mirror for {@link Object}
	 */
	ArrayType objectArray();

	/**
	 * Helper to check if this type is {@link Object}
	 *
	 * @param mirror The type to check
	 * @return If this type is exactly {@link Object}
	 * @see #object()
	 */
	boolean isObject(TypeMirror mirror);

	/**
	 * Helper to check if this type is an {@link Object} array
	 *
	 * @param mirror The type to check
	 * @return If this type is exactly an {@link Object} array
	 * @see #objectArray()
	 */
	boolean isObjectArray(TypeMirror mirror);

	/**
	 * Check if a type is primitive
	 *
	 * @param kind The type to check
	 * @return If the type is primitive
	 */
	boolean isPrimitive(TypeKind kind);
}
