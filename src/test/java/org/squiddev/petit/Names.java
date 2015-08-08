package org.squiddev.petit;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.junit.Test;
import org.squiddev.petit.annotation.Alias;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class Names {
	@Test
	public void testNames() {
		Embed example = new Embed();
		IPeripheral peripheral = PeripheralHelper.create(example);

		String[] names = new String[]{"foo", "bar", "test", "another", "thing"};
		Arrays.sort(names);

		String[] gotNames = peripheral.getMethodNames();
		Arrays.sort(gotNames);

		assertArrayEquals(names, gotNames);
	}

	@Peripheral("peripheral")
	public static class Embed {
		@LuaFunction
		@Alias("another")
		public String thing(String name) {
			return "Hello" + name;
		}

		@LuaFunction({"foo", "bar"})
		public void whoah() {
		}

		@LuaFunction
		public void test() {
		}
	}
}
