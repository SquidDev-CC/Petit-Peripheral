package org.squiddev.petit.api.tree.baked;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.Node;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public interface IArgumentBaked extends Node<VariableElement> {
	/**
	 * Get the kind for this argument.
	 *
	 * @return The argument's kind
	 */
	ArgumentKind getKind();

	/**
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	IMethodBaked getParent();

	/**
	 * Get the index this method exists at.
	 * For {@link ArgumentKind#PROVIDED} this is {@code -1}.
	 *
	 * @return The index this method exists at.
	 * @see IMethodBaked#getActualArguments()
	 */
	int getIndex();

	/**
	 * The type this argument takes
	 *
	 * This is the derived value, based of the value of {@link #getKind()}
	 *
	 * @return The type this argument takes
	 */
	TypeMirror getType();
}
