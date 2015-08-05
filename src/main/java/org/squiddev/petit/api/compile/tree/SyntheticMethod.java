package org.squiddev.petit.api.compile.tree;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;

/**
 * A synthetic method is attached to the generated class.
 *
 * There can be synthetic methods for one Java method, but only if the return type is {@code void}
 */
public interface SyntheticMethod extends Node<Element> {
	String ARG_PREFIX = "arg_";

	/**
	 * Get the types this method is suitable for
	 *
	 * @return The interfaces this method appears on
	 * @see org.squiddev.petit.api.compile.backend.Backend#compatibleWith(TypeMirror)
	 */
	Collection<TypeMirror> getBackends();

	/**
	 * Get the return type for this method
	 *
	 * @return The method's return type
	 */
	TypeMirror getReturnType();

	/**
	 * Get the parameters for a method.
	 * The argument's names will be simply {@code arg_0}, {@code arg_1}, etc... when generated.
	 *
	 * @return The parameters for a method
	 */
	List<TypeMirror> getParameters();

	/**
	 * Get the name of the method
	 *
	 * @return The method's name
	 */
	String getName();

	/**
	 * Convert the synthetic method into a code block.
	 *
	 * @return The generated code
	 */
	CodeBlock build();
}
