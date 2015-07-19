package org.squiddev.petit.conversion.from;

import org.squiddev.petit.processor.Segment;

/**
 * A converter that converts from Lua to Java values.
 */
public interface FromLuaConverter {
	/**
	 * Returns an expression that loads a value from {@code fromToken}, saves it to {@code toToken} and returns a
	 * boolean.
	 *
	 * Yeah, its a bit cumbersome.
	 *
	 * Example result: {@code <fromToken> instanceof String && (<toToken> = (String)<fromToken>) != null}
	 *
	 * @param fromToken The variable to load the value from
	 * @param toToken   The variable to save the value to
	 * @return The expression to write
	 */
	Segment convertFrom(String fromToken, String toToken);

	/**
	 * Get a friendly name of the type
	 *
	 * @return The type's name
	 */
	String getName();
}
