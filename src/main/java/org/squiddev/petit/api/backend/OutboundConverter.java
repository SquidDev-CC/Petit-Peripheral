package org.squiddev.petit.api.backend;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.tree.baked.IMethodBaked;

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
	 * If the method returns multiple values ({@link IMethodBaked#getVarReturn()}) then
	 * you should return an object rather than an object array.
	 *
	 * @param method The method we are converting for
	 * @param from   The expression that contains the value.
	 * @return The conversion expression
	 */
	CodeBlock convert(IMethodBaked method, String from);
}
