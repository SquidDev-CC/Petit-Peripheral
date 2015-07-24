package org.squiddev.petit.api.compile.tree;

import org.squiddev.petit.conversion.from.FromLuaConverter;

import javax.lang.model.element.VariableElement;

public interface Argument extends Node<VariableElement> {
	/**
	 * Get the type for this argument.
	 *
	 * @return The argument's type
	 * @see #setArgumentType(ArgumentType)
	 */
	ArgumentType getArgumentType();

	/**
	 * Set the type for this argument
	 *
	 * @param type The argument's type
	 * @see #getArgumentType()
	 */
	void setArgumentType(ArgumentType type);

	/**
	 * Get the converter for this argument
	 *
	 * @return The argument's converter, can be {@code null}
	 */
	FromLuaConverter getConverter();

	/**
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	PeripheralMethod getMethod();
}
