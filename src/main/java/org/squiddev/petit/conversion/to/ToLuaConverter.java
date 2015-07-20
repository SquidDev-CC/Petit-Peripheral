package org.squiddev.petit.conversion.to;

import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.TypeMirror;

/**
 * A converter that converts from Java to Lua values.
 */
public interface ToLuaConverter {
	/**
	 * If this converter matches the specified type
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
	 * Returns an expression that converts from {@code from}.
	 *
	 * You may also write to {@code to} if {@link #requiresVariable()} is true. However this will be overridden by the
	 * final result of this expression.
	 *
	 * @param from The expression that contains the value.
	 * @param to   {@code null} if {@link #requiresVariable()} is {@code false}, otherwise a variable you can write to.
	 * @return The conversion expression, or {@code null} if none is required.
	 */
	Segment convertTo(String from, String to);
}
