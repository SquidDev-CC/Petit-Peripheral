package org.squiddev.petit.verification;

import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

/**
 * Checks that multiple methods with the same name is blocked
 */
@Peripheral("test")
public class DuplicateMethods {
	@LuaFunction
	public void foo() {
	}

	@LuaFunction("foo")
	public void bar() {
	}
}
