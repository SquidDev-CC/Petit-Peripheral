package org.squiddev.petit;

import org.junit.Test;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

import static org.junit.Assert.assertEquals;

public class PetitParents {
	@Peripheral("peripheral")
	public static class Base {
		@LuaFunction
		public String overridden() {
			return "base";
		}

		@LuaFunction
		public String inherited() {
			return "inherited";
		}
	}

	public static class Parent extends Base {
		@Override
		@LuaFunction
		public String overridden() {
			return "parent";
		}
	}

	@Test
	public void ensureInherited() {
		assertEquals("inherited", PeripheralWrapper.create(new Parent()).call("inherited")[0]);
	}

	@Test
	public void ensureOverridden() {
		assertEquals("parent", PeripheralWrapper.create(new Parent()).call("overridden")[0]);
	}

	@Test
	public void inheritAttributes() {
		assertEquals("peripheral", PeripheralHelper.create(new Parent()).getType());
	}
}
