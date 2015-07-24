package org.squiddev.petit.api.compile.converter;

import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.TypeMirror;

/**
 * A converter that converts from Lua to Java values.
 */
public interface FromLuaConverter {
	/**
	 * If this getConverter matches the specified type
	 *
	 * @param type The type to match
	 * @return If this type is matched.
	 */
	boolean matches(TypeMirror type);

	/**
	 * Returns if an intermediate variable is required.
	 *
	 * @return The intermediate variable.
	 */
	boolean requiresVariable();

	/**
	 * Returns a segment that checks if {@code from} is valid.
	 *
	 * You may also write to {@code temp} if {@link #requiresVariable()} is true, instead of
	 * setting the variable in {@link #getValue(String, String)}
	 *
	 * @param from The expression that contains the value.
	 * @param temp {@code null} if {@link #requiresVariable()} is {@code false}, otherwise a variable you can write temp.
	 * @return The validation expression, or {@code null} if none is required.
	 */
	Segment validate(String from, String temp);

	/**
	 * Returns an expression that converts from {@code from}.
	 *
	 * You may read from {@code temp} if {@link #requiresVariable()} is true.
	 *
	 * @param from The expression that contains the value.
	 * @param temp {@code null} if {@link #requiresVariable()} is {@code false}, otherwise a variable you can read from.
	 * @return The conversion expression. Return {@code from} if none is required or {@code temp} if done earlier.
	 */
	Segment getValue(String from, String temp);

	/**
	 * Get a friendly name of the type
	 *
	 * @return The type's name
	 */
	String getName();
}
