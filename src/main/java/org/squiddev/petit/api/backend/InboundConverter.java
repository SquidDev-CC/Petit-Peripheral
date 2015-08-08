package org.squiddev.petit.api.backend;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;

import javax.lang.model.type.TypeMirror;

/**
 * A converter that converts from Lua to Java values.
 */
public interface InboundConverter {
	/**
	 * If this converter matches the specified type
	 *
	 * @param kind The argument kind
	 * @param type The type to match
	 * @return If this argument is matched.
	 */
	boolean matches(ArgumentKind kind, TypeMirror type);

	/**
	 * Get a friendly name of the type
	 *
	 * @return The type's name
	 */
	String getName();

	/**
	 * Write conversion preamble for an argument.
	 *
	 * This generally adds additional information.
	 *
	 * @param argument The argument information
	 * @return The CodeBlock that adds additional information, {@code null} if none is required.
	 */
	CodeBlock preamble(IArgumentBaked argument);

	/**
	 * Returns an expression or statement that checks if {@code from} is valid.
	 *
	 * If the result starts with {@code "$["} (JavaPoet's statement symbol) then this will
	 * be presumed to be a statement, otherwise an expression is used.
	 *
	 * For expressions, the result must be a boolean, for statements, an error must be thrown.
	 *
	 * @param argument The argument information
	 * @param from     The expression to convert from. If the argument is {@link ArgumentKind#PROVIDED} then this will be null.
	 * @return The CodeBlock that validates, {@code null} if none is required.
	 */
	Segment validate(IArgumentBaked argument, String from);

	/**
	 * Returns an expression that converts from {@code from}.
	 *
	 * @param argument The argument information
	 * @param from     The expression to convert from. If the argument is {@link ArgumentKind#PROVIDED} then this will be null.
	 * @return The CodeBlock that adds converts, {@code null} if none is required.
	 */
	CodeBlock convert(IArgumentBaked argument, String from);
}
