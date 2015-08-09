package org.squiddev.petit.verification;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.Handler;
import org.squiddev.petit.annotation.Peripheral;

/**
 * Check that optionals do not appear before required arguments
 */
@Peripheral("test")
public class ImplementIPeripheral implements IPeripheral {
	@Handler(IPeripheral.class)
	public void attach(IComputerAccess iComputerAccess) {

	}

	@Handler(IPeripheral.class)
	public Object[] callMethod(IComputerAccess iComputerAccess, ILuaContext iLuaContext, int i, Object[] objects) throws LuaException, InterruptedException {
		return new Object[0];
	}

	@Handler(IPeripheral.class)
	public void detach(IComputerAccess iComputerAccess) {

	}

	@Handler(IPeripheral.class)
	public boolean equals(IPeripheral iPeripheral) {
		return false;
	}

	@Handler(IPeripheral.class)
	public String[] getMethodNames() {
		return new String[0];
	}

	@Handler(IPeripheral.class)
	public String getType() {
		return null;
	}
}
