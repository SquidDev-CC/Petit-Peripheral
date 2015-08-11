package org.squiddev.petit;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.junit.Test;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests if peripheral methods are implemented
 */
public class PeripheralMethods {
	@Test
	public void correctName() {
		Embed example = new Embed("foo");
		IPeripheral peripheral = PeripheralHelper.create(example);
		assertEquals("peripheral", peripheral.getType());
	}

	@Test
	public void equals() {
		Embed example = new Embed("foo");
		IPeripheral a = PeripheralHelper.create(example);

		assertEquals(a, PeripheralHelper.create(example));
		assertEquals(a, PeripheralHelper.create(new Embed("foo")));
		assertNotEquals(a, PeripheralHelper.create(new Embed("bar")));
		assertNotEquals(a, "foo");
	}

	@Peripheral("peripheral")
	public static class Embed {
		protected final String name;

		public Embed(String name) {
			this.name = name;
		}

		@LuaFunction
		public void foo() {
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Embed && ((Embed) obj).name.equals(name);
		}
	}
}
