package org.squiddev.petit.backend.iperipheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.tree.MethodSignature;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;
import org.squiddev.petit.backend.tree.BakedValidator;
import org.squiddev.petit.backend.tree.BasicMethodSignature;

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
