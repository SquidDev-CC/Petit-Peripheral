package org.squiddev.petit.core.backend.iperipheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.IMethodSignature;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.base.tree.MethodSignature;
import org.squiddev.petit.core.backend.BakedVerifier;

import javax.tools.Diagnostic;
import java.util.*;

public class IPeripheralVerifier extends BakedVerifier {
	private final Set<IMethodSignature> blacklist;

	public IPeripheralVerifier(Backend backend, Environment environment) {
		super(backend, environment);
		blacklist = new HashSet<IMethodSignature>(Arrays.asList(
			new MethodSignature("equals", environment, IPeripheral.class),
			new MethodSignature("equals", environment, Object.class),
			new MethodSignature("getMethodNames", environment),
			new MethodSignature("getType", environment),
			new MethodSignature("callMethod", environment, IComputerAccess.class, ILuaContext.class, int.class, Object[].class)
		));
	}

	@Override
	public boolean verify(IClassBaked baked) {
		boolean success = super.verify(baked);

		for (Map.Entry<IMethodSignature, Collection<ISyntheticMethod>> methods : baked.getSyntheticMethods().entrySet()) {
			if (blacklist.contains(methods.getKey())) {
				success = false;

				String name = methods.getKey().getName();
				for (ISyntheticMethod method : methods.getValue()) {
					environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot implement " + name, method.getElement());
				}
			}
		}

		return success;
	}
}
