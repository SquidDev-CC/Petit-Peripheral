package org.squiddev.petit.verification;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.*;

/**
 * A class that should warn as there is no {@link org.squiddev.petit.annotation.Peripheral} annotation
 */
public class NoPeripheral {
	@Alias("foobar")
	public void alias() {
	}

	@Handler(IPeripheral.class)
	public void handler() {
	}

	@LuaFunction
	public void function() {
	}

	public void optional(@Optional String arg) {
	}

	public void provided(@Provided IComputerAccess arg) {
	}
}
