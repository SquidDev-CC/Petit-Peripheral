package org.squiddev.petit.api.compile.transformer.tree;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Node;

import javax.lang.model.element.TypeElement;
import java.util.Set;

public interface ClassBuilder extends Node<TypeElement> {
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
	Set<MethodBuilder> methods();
}
