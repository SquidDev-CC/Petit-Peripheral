package org.squiddev.petit.core.backend.iperipheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.MethodSignature;
import org.squiddev.petit.api.tree.SyntheticMethod;
import org.squiddev.petit.api.tree.baked.ClassBaked;
import org.squiddev.petit.base.tree.BasicMethodSignature;
import org.squiddev.petit.core.backend.BakedValidator;

import javax.tools.Diagnostic;
import java.util.*;

public class IPeripheralValidator extends BakedValidator {
	private final Set<MethodSignature> blacklist;

	public IPeripheralValidator(Backend backend, Environment environment) {
		super(backend, environment);
		blacklist = new HashSet<MethodSignature>(Arrays.asList(
			new BasicMethodSignature("equals", environment, IPeripheral.class),
			new BasicMethodSignature("getMethodNames", environment),
			new BasicMethodSignature("getType", environment),
			new BasicMethodSignature("callMethod", environment, IComputerAccess.class, ILuaContext.class, int.class, Object[].class)
		));
	}

	@Override
	public boolean validate(ClassBaked baked) {
		boolean success = super.validate(baked);

		for (Map.Entry<MethodSignature, Collection<SyntheticMethod>> methods : baked.getSyntheticMethods().entrySet()) {
			if (blacklist.contains(methods.getKey())) {
				success = false;

				String name = methods.getKey().getName();
				for (SyntheticMethod method : methods.getValue()) {
					environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot implement " + name, method.getElement());
				}
			}
		}

		return success;
	}
}
