package org.squiddev.petit.verification;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.Handler;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;

/**
 * Check that optionals do not appear before required arguments
 */
@Peripheral("test")
public class AccessLevels{
	@LuaFunction
	private void privateMethod() { }

	@LuaFunction
	public static void statusMethod() { }
}
