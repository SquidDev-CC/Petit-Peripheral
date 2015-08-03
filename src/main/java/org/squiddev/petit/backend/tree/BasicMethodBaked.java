package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;
import org.squiddev.petit.api.compile.tree.ArgumentKind;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BasicMethodBaked implements MethodBaked {
	private final Collection<String> names;
	private final List<ArgumentBaked> arguments;
	private final List<ArgumentBaked> actualArguments;
	private final ClassBaked parent;
	private final String error;
	private final boolean varReturn;
	private final ExecutableElement element;
	private final TypeMirror type;
	private final String target;

	public BasicMethodBaked(MethodBuilder builder, ClassBaked parent) {
		this.names = Collections.unmodifiableCollection(builder.names());
		this.parent = parent;
		this.error = builder.getErrorMessage();
		this.varReturn = builder.getVarReturn();
		this.element = builder.getElement();
		this.target = builder.getTarget();

		TypeMirror type = builder.getReturnType();
		if (builder.getVarReturn()) type = ((ArrayType) type).getComponentType();
		this.type = type;

		List<ArgumentBaked> arguments = new ArrayList<ArgumentBaked>();
		List<ArgumentBaked> actualArguments = new ArrayList<ArgumentBaked>();
		this.arguments = Collections.unmodifiableList(arguments);
		this.actualArguments = Collections.unmodifiableList(actualArguments);

		int index = 0;
		for (ArgumentBuilder argument : builder.getArguments()) {
			ArgumentBaked baked;
			if (argument.getKind() == ArgumentKind.PROVIDED) {
				baked = new BasicArgumentBaked(argument, -1, this);
			} else {
				baked = new BasicArgumentBaked(argument, index, this);
				actualArguments.add(baked);
				index++;
			}

			arguments.add(baked);
		}
	}

	@Override
	public Collection<String> getNames() {
		return names;
	}

	@Override
	public List<ArgumentBaked> getArguments() {
		return arguments;
	}

	@Override
	public List<ArgumentBaked> getActualArguments() {
		return actualArguments;
	}

	@Override
	public ClassBaked getParent() {
		return parent;
	}

	@Override
	public String getErrorMessage() {
		return error;
	}

	@Override
	public boolean getVarReturn() {
		return varReturn;
	}

	@Override
	public TypeMirror getReturnType() {
		return type;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public ExecutableElement getElement() {
		return element;
	}
}
