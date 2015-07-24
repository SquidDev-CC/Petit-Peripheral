package org.squiddev.petit.api.compile.tree;

import dan200.computercraft.api.peripheral.IPeripheral;

import javax.lang.model.element.TypeElement;
import java.util.Set;

public interface PeripheralClass extends Node<TypeElement> {
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
	Set<PeripheralMethod> methods();
}
