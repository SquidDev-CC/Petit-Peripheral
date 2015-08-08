package org.squiddev.petit.base.tree.builder;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Stores one argument of a Lua method
 */
public class ArgumentBuilder implements IArgumentBuilder {
	private final IMethodBuilder method;
	private final VariableElement parameter;
	private ArgumentKind kind = ArgumentKind.REQUIRED;

	public ArgumentBuilder(IMethodBuilder method, VariableElement parameter, ArgumentKind kind) {
		this.parameter = parameter;
		this.method = method;
		this.kind = kind;
	}

	@Override
	public ArgumentKind getKind() {
		return kind;
	}

	@Override
	public void setKind(ArgumentKind kind) {
		this.kind = kind;
	}

	@Override
	public TypeMirror getType() {
		return getElement().asType();
	}

	@Override
	public IMethodBuilder getParent() {
		return method;
	}

	@Override
	public VariableElement getElement() {
		return parameter;
	}

	@Override
	public String toString() {
		String start = "", end = "";
		switch (getKind()) {
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
