package org.squiddev.petit.core.transformer;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

/**
 * Validates a tree of objects
 */
public class BuilderValidator {
	protected final Environment environment;

	public BuilderValidator(Environment environment) {
		this.environment = environment;
	}

	public boolean validate(ClassBuilder builder) {
		boolean success = true;
		for (MethodBuilder method : builder.methods()) {
			success &= validate(method);
		}

		return success;
	}

	public boolean validate(MethodBuilder builder) {
		boolean success = true;
		Messager messager = environment.getMessager();

		ExecutableElement element = builder.getElement();

		if (!element.getModifiers().contains(Modifier.PUBLIC) || element.getModifiers().contains(Modifier.STATIC)) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Method must be public, non-static", element);
			success = false;
		}

		for (String name : builder.names()) {
			if (name.matches("^[a-zA-Z][a-z0-9A-Z]$")) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Invalid name '" + name + "'", element);
				success = false;
			}
		}

		if (builder.getVarReturn() && element.asType().getKind() != TypeKind.ARRAY) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Expected array for variable return", element);
			success = false;
		}

		ArgumentKind state = ArgumentKind.REQUIRED;
		for (ArgumentBuilder argument : builder.getArguments()) {
			switch (argument.getKind()) {
				case REQUIRED:
					if (state != ArgumentKind.REQUIRED) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected required argument after optional one", argument.getElement());
						success = false;
					}
					state = argument.getKind();
					break;
				case OPTIONAL:
					if (state == ArgumentKind.VARIABLE) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected optional argument after varargs", argument.getElement());
						success = false;
					}
					state = argument.getKind();
					break;
				case VARIABLE:
					state = argument.getKind();
					break;
				case PROVIDED:
					break;
				default:
					messager.printMessage(Diagnostic.Kind.WARNING, "Unknown variable kind " + argument.getKind() + ", this is an internal error", argument.getElement());
			}

			success &= validate(argument);
		}

		return success;
	}

	public boolean validate(ArgumentBuilder builder) {
		Messager messager = environment.getMessager();
		boolean success = true;

		ArgumentKind kind = builder.getKind();
		VariableElement element = builder.getElement();
		TypeKind typeKind = builder.getType().getKind();

		if (kind == ArgumentKind.VARIABLE && typeKind != TypeKind.ARRAY) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Expected array for varargs", element);
			success = false;
		}

		if (kind == ArgumentKind.OPTIONAL && environment.getTypeHelpers().isPrimitive(typeKind)) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Primitive cannot be optional", element);
			success = false;
		}

		return success;
	}
}
