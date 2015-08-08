package org.squiddev.petit.core.backend.iperipheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.base.tree.BasicSyntheticMethod;
import org.squiddev.petit.base.tree.baked.BasicClassBaked;

/**
 * Peripheral baker with additional information
 */
public class IPeripheralBaked extends BasicClassBaked {
	public IPeripheralBaked(String generatedName, ClassBuilder builder, Backend backend, Environment environment) {
		super(generatedName, builder, backend, environment);

		addSynthetic(
			new BasicSyntheticMethod.Builder("attach", getElement(), environment)
				.addBackends(IPeripheral.class)
				.addParameters(IComputerAccess.class)
				.returns(void.class)
				.build()
		);

		addSynthetic(
			new BasicSyntheticMethod.Builder("detach", getElement(), environment)
				.addBackends(IPeripheral.class)
				.addParameters(IComputerAccess.class)
				.returns(void.class)
				.build()
		);
	}
}
