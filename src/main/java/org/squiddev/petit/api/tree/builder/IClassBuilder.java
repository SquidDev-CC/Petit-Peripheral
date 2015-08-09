package org.squiddev.petit.api.tree.builder;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.Node;

import java.util.Collection;

public interface IClassBuilder extends Node {
	/**
	 * Get the name of the peripheral
	 *
	 * @return The peripheral's name
	 * @see IPeripheral#getType()
	 * @see Peripheral#value()
	 */
	String getName();

	/**
	 * Collection of peripheral methods
	 *
	 * @return The methods
	 */
	Collection<IMethodBuilder> methods();

	/**
	 * Collection of additional methods to attach to the generated class
	 *
	 * @return The synthetic methods
	 */
	Collection<ISyntheticMethod> syntheticMethods();
}
