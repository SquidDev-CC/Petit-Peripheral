package org.squiddev.petit.transformer.tree;

import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;
import org.squiddev.petit.api.compile.tree.ArgumentKind;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Stores one argument of a Lua method
 */
public class BasicArgumentBuilder implements ArgumentBuilder {
	private final MethodBuilder method;
	private final VariableElement parameter;
	private ArgumentKind kind = ArgumentKind.REQUIRED;

	public BasicArgumentBuilder(MethodBuilder method, VariableElement parameter, ArgumentKind kind) {
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
