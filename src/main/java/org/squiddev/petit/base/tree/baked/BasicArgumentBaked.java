package org.squiddev.petit.base.tree.baked;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.ArgumentBaked;
import org.squiddev.petit.api.tree.baked.MethodBaked;
import org.squiddev.petit.api.tree.builder.ArgumentBuilder;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

public class BasicArgumentBaked implements ArgumentBaked {
	private final ArgumentKind kind;
	private final MethodBaked parent;
	private final int index;
	private final VariableElement element;
	private final TypeMirror type;

	public BasicArgumentBaked(ArgumentKind kind, int index, VariableElement element, TypeMirror type, MethodBaked parent) {
		this.kind = kind;
		this.parent = parent;
		this.index = index;
		this.element = element;

		if (kind == ArgumentKind.VARIABLE) type = ((ArrayType) type).getComponentType();
		this.type = type;
	}

	public BasicArgumentBaked(ArgumentBuilder builder, int index, MethodBaked parent) {
		this(builder.getKind(), index, builder.getElement(), builder.getType(), parent);
	}

	@Override
	public ArgumentKind getKind() {
		return kind;
	}

	@Override
	public MethodBaked getParent() {
		return parent;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public TypeMirror getType() {
		return type;
	}

	@Override
	public VariableElement getElement() {
		return element;
	}
}
