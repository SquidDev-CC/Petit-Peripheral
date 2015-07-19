package org.squiddev.petit.conversion.to;

import org.squiddev.petit.processor.Segment;

/**
 * A converter that converts from Java to Lua values.
 */
public interface ToLuaConverter {
	/**
	 * Returns a collection of statements that assigns from {@code fromToken} and saves it to {@code toToken}.
	 *
	 * @param fromToken The variable to load the value from
	 * @param toToken   The variable to save the value to
	 * @return The statements to write
	 */
	Segment convertTo(String fromToken, String toToken);
}
