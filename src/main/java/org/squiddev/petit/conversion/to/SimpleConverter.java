package org.squiddev.petit.conversion.to;

import org.squiddev.petit.processor.Segment;

/**
 * Simply writes the result into a Object[]
 */
public class SimpleConverter implements ToLuaConverter {
	public static final ToLuaConverter instance = new SimpleConverter();

	@Override
	public Segment convertTo(String fromToken, String toToken) {
		return new Segment("$N = new Object[] { $N }", toToken, fromToken);
	}
}
