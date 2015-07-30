package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.ArgumentKind;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;

import javax.lang.model.element.VariableElement;

public class BasicArgumentBaked implements ArgumentBaked {
	private final ArgumentKind type;
	private final MethodBaked parent;
	private final int index;
	private final VariableElement element;

	public BasicArgumentBaked(ArgumentKind type, int index, VariableElement element, MethodBaked parent) {
		this.type = type;
		this.parent = parent;
		this.index = index;
		this.element = element;
	}

	public BasicArgumentBaked(ArgumentBuilder builder, int index, MethodBaked parent) {
		this(builder.getArgumentKind(), index, builder.getElement(), parent);
	}

	@Override
	public ArgumentKind getArgumentKind() {
		return type;
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
	public VariableElement getElement() {
		return element;
	}
}
