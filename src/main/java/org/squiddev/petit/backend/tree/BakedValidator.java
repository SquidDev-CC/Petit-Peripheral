package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;
import org.squiddev.petit.api.compile.tree.Validator;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BakedValidator implements Validator<ClassBaked> {
	protected final Backend backend;
	protected final Environment environment;

	public BakedValidator(Backend backend, Environment environment) {
		this.backend = backend;
		this.environment = environment;
	}

	@Override
	public boolean validate(ClassBaked baked) {
		Messager messager = environment.getMessager();
		boolean success = true;

		Set<String> names = new HashSet<String>();
		for (MethodBaked method : baked.getMethods()) {
			success &= validate(method);
			for (String name : method.getNames()) {
				if (!names.add(name)) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate name '" + name + "'", method.getElement());
					success = false;
				}
			}
		}

		for (Collection<SyntheticMethod> synthetics : baked.getSyntheticMethods().values()) {
			if (synthetics.size() > 1) {
				for (SyntheticMethod method : synthetics) {
					if (method.getReturnType().getKind() != TypeKind.VOID) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Multiple synthetic methods for non-void return type", method.getElement());
						success = false;
					}
				}
			}
		}

		return success;
	}

	protected boolean validate(MethodBaked baked) {
		boolean success = true;
		for (ArgumentBaked argument : baked.getArguments()) {
			success &= validate(argument);
		}
		return success;
	}

	protected boolean validate(ArgumentBaked baked) {
		if (backend.getInboundConverter(baked.getKind(), baked.getType()) == null) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "[" + backend + "] No converter", baked.getElement());
			return false;
		}

		return true;
	}
}
