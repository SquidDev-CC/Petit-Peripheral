package org.squiddev.petit.api.tree.builder;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.Node;

import javax.lang.model.type.TypeMirror;

public interface IArgumentBuilder extends Node {
	/**
	 * Get the kind for this argument.
	 *
	 * @return The argument's type
	 * @see #setKind(ArgumentKind)
	 */
	ArgumentKind getKind();

	/**
	 * Set the kind for this argument
	 *
	 * @param kind The argument's kind
	 * @see #getKind()
	 */
	void setKind(ArgumentKind kind);

	/**
	 * The type this argument takes
	 *
	 * This is the raw value and will not change based on the value of {@link #getKind()}
	 *
	 * @return The type this argument takes
	 */
	TypeMirror getType();

	/**
	 * Get the owning method for this argument
	 *
	 * @return The argument's method
	 */
	IMethodBuilder getParent();
}
