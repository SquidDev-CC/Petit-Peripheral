package org.squiddev.petit.verification;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.*;

/**
 * A class that should warn as there is no {@link Peripheral} annotation
 */
@Peripheral("test")
public class ArgumentOrder {
	@LuaFunction
	public void badOrder(@Optional String bar, String foo) {
	}
}
