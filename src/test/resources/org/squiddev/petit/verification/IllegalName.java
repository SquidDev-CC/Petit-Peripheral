package org.squiddev.petit.verification;

import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

/**
 * Check that optionals do not appear before required arguments
 */
@Peripheral("test")
public class IllegalName {
	@LuaFunction("foo-bar")
	public void a() {
	}

	@LuaFunction("0foo")
	public void b() {
	}
}
