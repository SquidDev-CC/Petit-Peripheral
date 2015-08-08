package org.squiddev.petit.api.tree;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * A tiny signature for methods.
 *
 * This should implement {@link Object#hashCode()} and {@link Object#equals(Object)}
 */
public interface MethodSignature {
	/**
	 * Get the parameters for a method.
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
}
