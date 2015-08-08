package org.squiddev.petit.api.tree.builder;

import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.Node;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;

public interface IMethodBuilder extends Node<ExecutableElement> {
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
	List<IArgumentBuilder> getArguments();

	/**
	 * Get the owning class for this method
	 *
	 * @return The methods's class
	 */
	IClassBuilder getParent();

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

	/**
	 * Get the return type for this method
	 *
	 * This is the raw value and will not change based on the value of {@link #getVarReturn()}
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
