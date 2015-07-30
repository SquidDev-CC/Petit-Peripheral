package org.squiddev.petit.api.compile.backend.tree;

import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.ExecutableElement;
import java.util.Collection;
import java.util.List;

public interface MethodBaked extends Node<ExecutableElement> {
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
	List<ArgumentBaked> getArguments();

	/**
	 * Get the arguments that must be specified - non
	 * provided arguments
	 *
	 * @return This method's arguments
	 */
	List<ArgumentBaked> getActualArguments();

	/**
	 * Get the owning class for this method
	 *
	 * @return The methods's class
	 */
	ClassBaked getParent();

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
}
