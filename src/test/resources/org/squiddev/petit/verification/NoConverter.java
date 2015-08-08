package org.squiddev.petit.verification;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.annotation.Provided;
import org.squiddev.petit.api.Environment;

/**
 * A class that should warn as there is no {@link Peripheral} annotation
 */
@Peripheral("test")
public class NoConverter {
	@LuaFunction
	public void noConverter(Environment noConverter) {
	}

	@LuaFunction
	public void notProvided(IComputerAccess notProvided) {
	}

	@LuaFunction
	public void noProvider(@Provided String noProvider) {
	}
}
