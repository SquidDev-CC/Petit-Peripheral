package org.squiddev.petit.base.tree.baked;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

public class ArgumentBaked implements IArgumentBaked {
	private final ArgumentKind kind;
	private final IMethodBaked parent;
	private final int index;
	private final Element element;
	private final TypeMirror type;

	public ArgumentBaked(ArgumentKind kind, int index, Element element, TypeMirror type, IMethodBaked parent) {
		this.kind = kind;
		this.parent = parent;
		this.index = index;
		this.element = element;

		if (kind == ArgumentKind.VARIABLE) type = ((ArrayType) type).getComponentType();
		this.type = type;
	}

	public ArgumentBaked(IArgumentBuilder builder, int index, IMethodBaked parent) {
		this(builder.getKind(), index, builder.getElement(), builder.getType(), parent);
	}

	@Override
	public ArgumentKind getKind() {
		return kind;
	}

	@Override
	public IMethodBaked getParent() {
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
	public Element getElement() {
		return element;
	}
}
