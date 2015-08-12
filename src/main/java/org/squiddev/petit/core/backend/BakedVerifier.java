package org.squiddev.petit.core.backend;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.Verifier;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BakedVerifier implements Verifier<IClassBaked> {
	protected final Backend backend;
	protected final Environment environment;

	public BakedVerifier(Backend backend, Environment environment) {
		this.backend = backend;
		this.environment = environment;
	}

	@Override
	public boolean verify(IClassBaked baked) {
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

		if (baked.getMethods().size() == 0) {
			messager.printMessage(Diagnostic.Kind.ERROR, "No methods for peripheral", baked.getElement());
			success = false;
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

		TypeMirror superClass = null;
		for (DeclaredType parent : baked.getParents()) {
			Element element = parent.asElement();
			if (element.getKind() == ElementKind.CLASS) {
				if (superClass != null) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Multiple super classes: " + parent + " and " + superClass, baked.getElement());
					success = false;
				}
				superClass = parent;
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
