package org.squiddev.petit.api.compile.transformer.tree;

import org.squiddev.petit.api.compile.ArgumentKind;
import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.VariableElement;

public interface ArgumentBuilder extends Node<VariableElement> {
	/**
	 * Get the kind for this argument.
	 *
	 * @return The argument's type
	 * @see #setArgumentType(ArgumentKind)
	 */
	ArgumentKind getArgumentKind();

	/**
	 * Set the kind for this argument
	 *
	 * @param type The argument's kind
	 * @see #getArgumentKind()
	 */
	void setArgumentType(ArgumentKind type);

	/**
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	MethodBuilder getParent();
}
