package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;

import javax.annotation.processing.Messager;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

public class BakedValidator {
	public boolean validate(ClassBaked baked, Environment environment, Backend backend) {
		Messager messager = environment.getMessager();
		boolean success = true;

		Set<String> names = new HashSet<String>();
		for (MethodBaked method : baked.getMethods()) {
			success &= validate(method, environment, backend);
			for (String name : method.getNames()) {
				if (!names.add(name)) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate name '" + name + "'", method.getElement());
					success = false;
				}
			}
		}

		return success;
	}

	public boolean validate(MethodBaked baked, Environment environment, Backend backend) {
		boolean success = true;
		for (ArgumentBaked argument : baked.getArguments()) {
			success &= validate(argument, environment, backend);
		}
		return success;
	}

	public boolean validate(ArgumentBaked baked, Environment environment, Backend backend) {
		TypeMirror type = baked.getElement().asType();
		if (baked.getArgumentType() == ArgumentType.VARIABLE) {
			type = ((ArrayType) type).getComponentType();
		}

		if (backend.getInboundConverter(type) == null) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "[" + backend + "] No converter", baked.getElement());
			return false;
		}

		return true;
	}
}
