package org.squiddev.petit.transformer.tree;

import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

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
	public boolean validate(ClassBuilder builder, Environment environment) {
		boolean success = true;
		for (MethodBuilder method : builder.methods()) {
			success &= validate(method, environment);
		}

		return success;
	}

	public boolean validate(MethodBuilder builder, Environment environment) {
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

		ArgumentType state = ArgumentType.REQUIRED;
		for (ArgumentBuilder argument : builder.getArguments()) {
			switch (argument.getArgumentType()) {
				case REQUIRED:
					if (state != ArgumentType.REQUIRED) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected required argument after optional one", argument.getElement());
						success = false;
					}
					state = argument.getArgumentType();
					break;
				case OPTIONAL:
					if (state == ArgumentType.VARIABLE) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected optional argument after varargs", argument.getElement());
						success = false;
					}
					state = argument.getArgumentType();
					break;
				case VARIABLE:
					state = argument.getArgumentType();
					break;
				case PROVIDED:
					break;
				default:
					messager.printMessage(Diagnostic.Kind.WARNING, "Unknown variable kind " + argument.getArgumentType() + ", this is an internal error", argument.getElement());
			}

			success &= validate(argument, environment);
		}

		return success;
	}

	public boolean validate(ArgumentBuilder builder, Environment environment) {
		Messager messager = environment.getMessager();
		boolean success = true;

		ArgumentType type = builder.getArgumentType();
		VariableElement element = builder.getElement();


		if (type == ArgumentType.VARIABLE && element.asType().getKind() != TypeKind.ARRAY) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Expected array for varargs", element);
			success = false;
		}

		if (type == ArgumentType.OPTIONAL && environment.getTypeHelpers().isPrimitive(element.asType().getKind())) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Primitive cannot be optional", element);
			success = false;
		}

		return success;
	}
}
