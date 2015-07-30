package org.squiddev.petit.api.compile.transformer.tree;

import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.VariableElement;

public interface ArgumentBuilder extends Node<VariableElement> {
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
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	MethodBuilder getParent();
}
