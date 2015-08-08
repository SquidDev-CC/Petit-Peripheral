package org.squiddev.petit.api.backend;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.TypeMirror;

/**
 * A converter that converts from Java to Lua values.
 */
public interface OutboundConverter {
	/**
	 * If this converter matches the specified type
	 *
	 * @param type The type to match
	 * @return If this type is matched.
	 */
	boolean matches(TypeMirror type);

	/**
	 * Returns an expression that converts from {@code from}.
	 *
	 * @param from The expression that contains the value.
	 * @return The conversion expression, or {@code null} if none is required.
	 */
	CodeBlock convertTo(String from);
}
