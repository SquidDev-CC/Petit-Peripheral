package org.squiddev.petit.api.compile.transformer.tree;

import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.ExecutableElement;
import java.util.Collection;
import java.util.List;

public interface MethodBuilder extends Node<ExecutableElement> {
	/**
	 * Get a writable collection of names of this method
	 *
	 * @return This method's names
	 */
	Collection<String> names();

	/**
	 * Get a readonly list of arguments for this method
	 *
	 * @return This method's arguments
	 */
	List<ArgumentBuilder> getArguments();

	/**
	 * Get the owning class for this method
	 *
	 * @return The methods's class
	 */
	ClassBuilder getParent();

	/**
	 * Get the error message thrown on validation errors
	 *
	 * @return The method's error message
	 * @see #setErrorMessage(String)
	 */
	String getErrorMessage();

	/**
	 * Set the error message throw on validation errors
	 *
	 * @param message The method's error message
	 * @see #getErrorMessage()
	 */
	void setErrorMessage(String message);

	/**
	 * Get if the method returns multiple values.
	 *
	 * This is only needed if an array is returned. By
	 * default the array will be wrapped as a table.
	 *
	 * @return If the method returns multiple values
	 * @see #setVarReturn(boolean)
	 */
	boolean getVarReturn();

	/**
	 * Get if the method returns multiple values.
	 *
	 * This is only needed if an array is returned. By
	 * default the array will be wrapped as a table.
	 *
	 * @param varReturn If the method returns multiple values
	 * @see #getVarReturn()
	 */
	void setVarReturn(boolean varReturn);
}
