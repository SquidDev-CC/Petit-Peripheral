package org.squiddev.petit;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.junit.Test;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

import static org.junit.Assert.*;

/**
 * Tests if peripheral methods are implemented
 */
public class PeripheralMethods {
	@Test
	public void correctName() {
		Embed example = new Embed();
		IPeripheral peripheral = PeripheralHelper.create(example);
		assertEquals("peripheral", peripheral.getType());
	}

	@Test
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	public void equals() {
		Embed example = new Embed();
		IPeripheral a = PeripheralHelper.create(example);

		assertTrue(a.equals(PeripheralHelper.create(example)));
		assertFalse(a.equals(PeripheralHelper.create(new Embed())));
		assertFalse(a.equals("foo"));
	}

	@Peripheral("peripheral")
	public static class Embed {
		@LuaFunction
		public void foo() {
		}
	}
}
