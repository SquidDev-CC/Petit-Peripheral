package org.squiddev.petit.api.compile.backend.tree;

import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.VariableElement;

public interface ArgumentBaked extends Node<VariableElement> {
	/**
	 * Get the type for this argument.
	 *
	 * @return The argument's type
	 */
	ArgumentType getArgumentType();

	/**
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	MethodBaked getParent();

	/**
	 * Get the index this method exists at.
	 * For {@link ArgumentType#PROVIDED} this is {@code -1}.
	 *
	 * @return The index this method exists at.
	 * @see MethodBaked#getActualArguments()
	 */
	int getIndex();
}
