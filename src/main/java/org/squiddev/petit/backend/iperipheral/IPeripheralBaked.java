package org.squiddev.petit.backend.iperipheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.backend.tree.BasicClassBaked;
import org.squiddev.petit.backend.tree.BasicSyntheticMethod;

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
