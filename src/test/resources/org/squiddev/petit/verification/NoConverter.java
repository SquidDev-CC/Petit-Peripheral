package org.squiddev.petit.verification;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.annotation.Provided;
import org.squiddev.petit.api.Environment;

/**
 * Check that converters
 */
@Peripheral("test")
public class NoConverter {
	@LuaFunction
	public void noConverter(Environment noConverter) {
	}

	@LuaFunction
	public void notProvided(IComputerAccess notProvided) {
	}

	/**
	 * This one will not produce an error as the backend cannot find a
	 * converter for it.
	 * This is desired behaviour.
	 * TODO: Report a warning if no backend accepts this type
	 */
	@LuaFunction
	public void noProvider(@Provided String noProvider) {
	}
}
