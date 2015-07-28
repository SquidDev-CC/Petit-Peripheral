package org.squiddev.petit;


import org.junit.Test;
import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.Optional;
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

	@Test
	public void defaultMode() {
		wrapper.call("defaultMode", 1, 2);
		wrapper.call("defaultMode", 1, 2, 3);
		wrapper.call("defaultMode", 1, 2.0);
		wrapper.call("defaultMode", 1, 2.0f);
		wrapper.call("defaultMode", (byte) 1, (long) 2);

		ExpectException.expect(
			"Expected number, number",
			wrapper.runMethod("defaultMode"),
			wrapper.runMethod("defaultMode", "foo"),
			wrapper.runMethod("defaultMode", 1.0, false),
			wrapper.runMethod("defaultMode", "foo", 2)
		);
	}

	@Test
	public void testingError() {
		wrapper.call("testingError", 1, "a");
		wrapper.call("testingError", 1, "b", "something");

		ExpectException.expect(
			"I expected better of you!",
			wrapper.runMethod("testingError"),
			wrapper.runMethod("testingError", "foo"),
			wrapper.runMethod("testingError", 1, false),
			wrapper.runMethod("testingError", "foo", 2),
			wrapper.runMethod("testingError", 1, ""),
			wrapper.runMethod("testingError", 1, "  ")
		);
	}

	@Test
	public void variableTyped() {
		wrapper.call("variableTyped", 1);
		wrapper.call("variableTyped", 1, "foo");
		wrapper.call("variableTyped", 1, "foo", "bar");

		ExpectException.expect(
			"Expected number, [string...]",
			wrapper.runMethod("variableTyped"),
			wrapper.runMethod("variableTyped", "foo"),
			wrapper.runMethod("variableTyped", 1, false),
			wrapper.runMethod("variableTyped", "foo", 2),
			wrapper.runMethod("variableTyped", 1, "foo", 2)
		);
	}

	@Test
	public void optional() {
		wrapper.call("optional", 1);
		wrapper.call("optional", 1, "foo");
		wrapper.call("optional", 1, "foo", "bar");

		ExpectException.expect(
			"Expected number, [string], [string]",
			wrapper.runMethod("optional"),
			wrapper.runMethod("optional", "foo"),
			wrapper.runMethod("optional", 1, false),
			wrapper.runMethod("optional", "foo", 2),
			wrapper.runMethod("optional", 1, "foo", 2),
			wrapper.runMethod("optional", 1, null, "bar")
		);
	}

	@Test
	public void boxed() {
		wrapper.call("boxed", 1, 2);
		wrapper.call("boxed", 1, 2, 3);
		wrapper.call("boxed", 1, 2.0);
		wrapper.call("boxed", 1, 2.0f);
		wrapper.call("boxed", (byte) 1, (long) 2);

		ExpectException.expect(
			"Expected number, number",
			wrapper.runMethod("boxed"),
			wrapper.runMethod("boxed", "foo"),
			wrapper.runMethod("boxed", 1.0, false),
			wrapper.runMethod("boxed", "foo", 2)
		);
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
		public void testingError(double a, char b) {
		}

		@LuaFunction
		public void variableTyped(double a, String... foo) {
		}

		@LuaFunction
		public void optional(double a, @Optional String foo, @Optional String bar) {
		}

		@LuaFunction
		public void boxed(Double a, Integer b) {
		}
	}
}
