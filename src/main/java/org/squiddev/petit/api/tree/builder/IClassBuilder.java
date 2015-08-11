package org.squiddev.petit.api.tree.builder;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.Node;
import org.squiddev.petit.api.tree.ParentKind;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Map;

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

	/**
	 * Collection of 'parent' implementations
	 *
	 * This is entirely up to the backend on how to handle this.
	 * This will be loaded from the interface and extends list by
	 * default
	 *
	 * @return The 'parent' implementations
	 */
	Collection<Map.Entry<TypeMirror, ParentKind>> parents();
}
