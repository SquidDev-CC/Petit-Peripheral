package org.squiddev.petit;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.junit.Test;
import org.squiddev.petit.annotation.Extends;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

import static org.junit.Assert.*;

public class VanillaParents {

	@Test
	public void checksImplements() {
		assertTrue(PeripheralHelper.create(new Embed()) instanceof Random);
	}

	@Test
	public void checksExtends() {
		assertTrue(PeripheralHelper.create(new Embed()) instanceof RandomPeripheral);
	}

	@Test
	public void inheritsMethodNames() {
		assertArrayEquals(new String[]{"foo", "bar", "stub"}, PeripheralHelper.create(new Embed()).getMethodNames());
	}

	@Test
	public void callsSuper() {
		RandomPeripheral peripheral = (RandomPeripheral) PeripheralHelper.create(new Embed());
		peripheral.attach(null);
		peripheral.attach(null);
		assertEquals(2, peripheral.called);
	}

	@Test
	public void inheritsMethods() {
		assertEquals("foo", PeripheralWrapper.create(new Embed()).call("foo")[0]);
		assertEquals("bar", PeripheralWrapper.create(new Embed()).call("bar")[0]);
	}


	@Test
	public void newMethods() {
		assertEquals("stub", PeripheralWrapper.create(new Embed()).call("stub")[0]);
	}

	@Peripheral("peripheral")
	@Extends({Random.class, RandomPeripheral.class})
	public static class Embed {
		@LuaFunction
		public String stub() {
			return "stub";
		}
	}

	public interface Random {
	}

	public static class RandomPeripheral implements IPeripheral {
		private int called = 0;

		@Override
		public String getType() {
			return "foo";
		}

		@Override
		public String[] getMethodNames() {
			return new String[]{"foo", "bar"};
		}

		@Override
		public Object[] callMethod(IComputerAccess iComputerAccess, ILuaContext iLuaContext, int i, Object[] objects) throws LuaException, InterruptedException {
			switch (i) {
				case 0:
					return new Object[]{"foo"};
				case 1:
					return new Object[]{"bar"};
			}

			return null;
		}

		@Override
		public void attach(IComputerAccess iComputerAccess) {
			called++;
		}

		@Override
		public void detach(IComputerAccess iComputerAccess) {
		}

		@Override
		public boolean equals(IPeripheral iPeripheral) {
			return false;
		}
	}
}
