package org.squiddev.petit;


import org.junit.Test;
import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.Peripheral;

public class Validation {
	public final PeripheralWrapper wrapper = new PeripheralWrapper(PeripheralHelper.create(new Embed()));

	@Test
	public void emptyArguments() {
		wrapper.call("emptyArguments");
		wrapper.call("emptyArguments", "foo");
		wrapper.call("emptyArguments", 1);
		wrapper.call("emptyArguments", 1, 2, 3);
	}

	@Test
	public void array() {
		wrapper.call("array");
		wrapper.call("array", "foo");
		wrapper.call("array", 1);
		wrapper.call("array", 1, 2, 3);
	}

	@Peripheral("peripheral")
	public static class Embed {
		@LuaFunction
		public void emptyArguments() {
		}

		@LuaFunction
		public void array(Object... args) {
		}

		@LuaFunction
		public void defaultMode(double a, int b) {
		}

		@LuaFunction(error = "I expected better of you!")
		public void testingError(double a, int b) {
		}
	}
}
