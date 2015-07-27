package org.squiddev.petit.api.compile.writer;

import com.squareup.javapoet.TypeSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.tree.PeripheralClass;

/**
 * The base class for writes
 */
public interface Writer {
	/**
	 * The argument name that stores the {@code Object[]} of arguments.
	 *
	 * @see IPeripheral#callMethod(IComputerAccess, ILuaContext, int, Object[])
	 */
	String ARG_ARGS = "args";

	/**
	 * The argument name that stores the {@see ILuaContext} argument.
	 *
	 * @see IPeripheral#callMethod(IComputerAccess, ILuaContext, int, Object[])
	 */
	String ARG_LUA_CONTEXT = "context";

	/**
	 * The argument name that stores the {@see ILuaContext} argument.
	 *
	 * @see IPeripheral#callMethod(IComputerAccess, ILuaContext, int, Object[])
	 */
	String ARG_COMPUTER = "computer";

	TypeSpec.Builder writeClass(PeripheralClass klass);
}
