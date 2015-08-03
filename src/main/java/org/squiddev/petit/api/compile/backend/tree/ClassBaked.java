package org.squiddev.petit.api.compile.backend.tree;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.tree.MethodSignature;
import org.squiddev.petit.api.compile.tree.Node;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Map;

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

	/**
	 * Collection of additional methods to attach to the generated class
	 *
	 * @return The synthetic methods
	 */
	Map<MethodSignature, Collection<SyntheticMethod>> getSyntheticMethods();
}
