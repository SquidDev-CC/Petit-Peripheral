package org.squiddev.petit.core.backend;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.Validator;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BakedValidator implements Validator<IClassBaked> {
	protected final Backend backend;
	protected final Environment environment;

	public BakedValidator(Backend backend, Environment environment) {
		this.backend = backend;
		this.environment = environment;
	}

	@Override
	public boolean validate(IClassBaked baked) {
		Messager messager = environment.getMessager();
		boolean success = true;

		Set<String> names = new HashSet<String>();
		for (IMethodBaked method : baked.getMethods()) {
			success &= validate(method);
			for (String name : method.getNames()) {
				if (!names.add(name)) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate name '" + name + "'", method.getElement());
					success = false;
				}
			}
		}

		for (Collection<ISyntheticMethod> synthetics : baked.getSyntheticMethods().values()) {
			if (synthetics.size() > 1) {
				for (ISyntheticMethod method : synthetics) {
					if (method.getReturnType().getKind() != TypeKind.VOID) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Multiple synthetic methods for non-void return type", method.getElement());
						success = false;
					}
				}
			}
		}

		return success;
	}

	protected boolean validate(IMethodBaked baked) {
		boolean success = true;
		for (IArgumentBaked argument : baked.getArguments()) {
			success &= validate(argument);
		}
		return success;
	}

	protected boolean validate(IArgumentBaked baked) {
		if (backend.getInboundConverter(baked.getKind(), baked.getType()) == null) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "[" + backend + "] No converter", baked.getElement());
			return false;
		}

		return true;
	}
}
