package org.squiddev.petit.transformer.tree;

import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

import javax.lang.model.element.VariableElement;

/**
 * Stores one argument of a Lua method
 */
public class BasicArgumentBuilder implements ArgumentBuilder {
	private final MethodBuilder method;
	private final VariableElement parameter;
	private ArgumentType type = ArgumentType.REQUIRED;

	public BasicArgumentBuilder(MethodBuilder method, VariableElement parameter, ArgumentType type) {
		this.parameter = parameter;
		this.method = method;
		this.type = type;
	}

	@Override
	public ArgumentType getArgumentType() {
		return type;
	}

	@Override
	public void setArgumentType(ArgumentType type) {
		this.type = type;
	}

	@Override
	public MethodBuilder getParent() {
		return method;
	}

	@Override
	public VariableElement getElement() {
		return parameter;
	}

	@Override
	public String toString() {
		String start = "", end = "";
		switch (getArgumentType()) {
			case OPTIONAL:
				start = "[";
				end = "]";
				break;
			case VARIABLE:
				start = "[";
				end = "...]";
				break;
			case PROVIDED:
				start = "{";
				end = "}";
		}

		return start + parameter.getSimpleName() + ":" + parameter.asType() + end;
	}
}
