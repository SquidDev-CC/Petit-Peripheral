package org.squiddev.petit.api.tree.baked;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.tree.IMethodSignature;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.Node;

import javax.lang.model.type.DeclaredType;
import java.util.Collection;
import java.util.Map;

public interface IClassBaked extends Node {
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
	 * @see Peripheral#value()
	 */
	String getName();

	/**
	 * Collection of peripheral methods
	 *
	 * @return The methods
	 */
	Collection<IMethodBaked> getMethods();

	/**
	 * Collection of additional methods to attach to the generated class
	 *
	 * @return The synthetic methods
	 */
	Map<IMethodSignature, Collection<ISyntheticMethod>> getSyntheticMethods();

	/**
	 * Get the parent classes/interfaces for this type
	 *
	 * @return The parent types
	 */
	Collection<DeclaredType> getParents();
}
