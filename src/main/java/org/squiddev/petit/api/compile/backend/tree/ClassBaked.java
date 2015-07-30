package org.squiddev.petit.api.compile.backend.tree;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

public interface ClassBaked extends Node<TypeElement> {
	/**
	 * Get the name of the generated class
	 *
	 * @return The name of the generated class
	 */
	String getGeneratedName();

	/**
	 * Get the name of the peripheral
	 *
	 * @return The peripheral's name
	 * @see IPeripheral#getType()
	 * @see org.squiddev.petit.api.Peripheral#value()
	 */
	String getName();

	/**
	 * Collection of peripheral methods
	 *
	 * @return The methods
	 */
	Collection<MethodBaked> getMethods();
}