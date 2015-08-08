package org.squiddev.petit.core.backend.iperipheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.base.tree.SyntheticMethod;
import org.squiddev.petit.base.tree.baked.ClassBaked;

/**
 * Peripheral baker with additional information
 */
public class IPeripheralBaked extends ClassBaked {
	public IPeripheralBaked(String generatedName, IClassBuilder builder, Backend backend, Environment environment) {
		super(generatedName, builder, backend, environment);

		addSynthetic(
			new SyntheticMethod.Builder("attach", getElement(), environment)
				.addBackends(IPeripheral.class)
				.addParameters(IComputerAccess.class)
				.returns(void.class)
				.build()
		);

		addSynthetic(
			new SyntheticMethod.Builder("detach", getElement(), environment)
				.addBackends(IPeripheral.class)
				.addParameters(IComputerAccess.class)
				.returns(void.class)
				.build()
		);
	}
}
