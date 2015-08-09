package org.squiddev.petit.verification;

import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Optional;
import org.squiddev.petit.annotation.Peripheral;

/**
 * Check that optionals do not appear before required arguments
 */
@Peripheral("test")
public class ArgumentOrder {
	@LuaFunction
	public void badOrder(@Optional String bar, String foo) {
	}
}
