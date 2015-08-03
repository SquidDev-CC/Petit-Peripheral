package org.squiddev.petit.api.compile.transformer.tree;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.tree.Node;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

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
	Collection<MethodBuilder> methods();

	/**
	 * Collection of additional methods to attach to the generated class
	 *
	 * @return The synthetic methods
	 */
	Collection<SyntheticMethod> syntheticMethods();
}
