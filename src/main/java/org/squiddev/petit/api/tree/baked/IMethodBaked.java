package org.squiddev.petit.api.tree.baked;

import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.Node;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;

public interface IMethodBaked extends Node {
	/**
	 * Get the names of this method
	 *
	 * @return This method's names
	 */
	Collection<String> getNames();

	/**
	 * Get the arguments for this method
	 *
	 * @return This method's arguments
	 */
	List<IArgumentBaked> getArguments();

	/**
	 * Get the arguments that must be specified - non
	 * provided arguments
	 *
	 * @return This method's arguments
	 */
	List<IArgumentBaked> getActualArguments();

	/**
	 * Get the owning class for this method
	 *
	 * @return The methods's class
	 */
	IClassBaked getParent();

	/**
	 * Get the error message thrown on validation errors
	 *
	 * @return The method's error message
	 */
	String getErrorMessage();

	/**
	 * Get if the method returns multiple values.
	 *
	 * This is only needed if an array is returned. By
	 * default the array will be wrapped as a table.
	 *
	 * @return If the method returns multiple values
	 * @see #getVarReturn()
	 */
	boolean getVarReturn();

	/**
	 * Get the return type for this method
	 *
	 * This is the derived value, based of the value of {@link #getVarReturn()}
	 *
	 * @return The method's return type.
	 */
	TypeMirror getReturnType();

	/**
	 * Get the target method for this function.
	 *
	 * This is the method that will be called in the end, this may be a chain of
	 * other methods ({@code foo().bar().baz} or an object ({@code foo.baz}).
	 *
	 * @return The target method
	 * @see Backend#FIELD_INSTANCE
	 */
	String getTarget();
}
