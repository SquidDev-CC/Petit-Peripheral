package org.squiddev.petit.api.compile.writer;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

/**
 * A specialised writer that writes to {@link IPeripheral} instances
 */
public interface PeripheralWriter extends Writer {
	/**
	 * Writes the function to get the peripheral's method
	 *
	 * @param klass The class we are writing for
	 * @return The implementation of getMethodNames
	 * @see IPeripheral#getMethodNames()
	 */
	MethodSpec writeMethodNames(PeripheralClass klass);

	/**
	 * Write the equals method
	 *
	 * @param klass The class we are writing for
	 * @return The implementation of equals
	 * @see IPeripheral#equals(IPeripheral)
	 */
	MethodSpec writeEquals(PeripheralClass klass);

	/**
	 * Writes the function to get the peripheral's name
	 *
	 * @param klass The class we are writing for
	 * @return The implementation of getType
	 * @see IPeripheral#getType()
	 */
	MethodSpec writeType(PeripheralClass klass);

	/**
	 * Writes the main call method
	 *
	 * @param klass The class we are writing for
	 * @return The implementation of callMethod
	 * @see IPeripheral#callMethod(IComputerAccess, ILuaContext, int, Object[])
	 */
	MethodSpec writeCall(PeripheralClass klass);

	/**
	 * Write the call block for one method
	 *
	 * @param method The method to write for
	 * @return The block for one method
	 */
	CodeBlock writeMethod(PeripheralMethod method);
}
