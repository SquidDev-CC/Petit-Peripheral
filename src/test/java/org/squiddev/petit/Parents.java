package org.squiddev.petit;

import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

public class Parents {
	@Peripheral("peripheral")
	public static class Base {
		@LuaFunction
		public String method() {
			return "foo";
		}

		@LuaFunction
		public String inherited() {
			return "bar";
		}
	}

	public static class Parent extends Base {
		@Override
		@LuaFunction
		public String method() {
			return "bar";
		}
	}
}
