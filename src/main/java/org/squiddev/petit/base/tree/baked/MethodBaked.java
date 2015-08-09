package org.squiddev.petit.base.tree.baked;

import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MethodBaked implements IMethodBaked {
	private final Collection<String> names;
	private final List<IArgumentBaked> arguments;
	private final List<IArgumentBaked> actualArguments;
	private final IClassBaked parent;
	private final String error;
	private final boolean varReturn;
	private final Element element;
	private final TypeMirror type;
	private final String target;

	public MethodBaked(IMethodBuilder builder, IClassBaked parent) {
		this.names = Collections.unmodifiableCollection(builder.names());
		this.parent = parent;
		this.error = builder.getErrorMessage();
		this.varReturn = builder.getVarReturn();
		this.element = builder.getElement();
		this.target = builder.getTarget();

		TypeMirror type = builder.getReturnType();
		if (builder.getVarReturn()) type = ((ArrayType) type).getComponentType();
		this.type = type;

		List<IArgumentBaked> arguments = new ArrayList<IArgumentBaked>();
		List<IArgumentBaked> actualArguments = new ArrayList<IArgumentBaked>();
		this.arguments = Collections.unmodifiableList(arguments);
		this.actualArguments = Collections.unmodifiableList(actualArguments);

		int index = 0;
		for (IArgumentBuilder argument : builder.getArguments()) {
			IArgumentBaked baked;
			if (argument.getKind() == ArgumentKind.PROVIDED) {
				baked = new ArgumentBaked(argument, -1, this);
			} else {
				baked = new ArgumentBaked(argument, index, this);
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
	public List<IArgumentBaked> getArguments() {
		return arguments;
	}

	@Override
	public List<IArgumentBaked> getActualArguments() {
		return actualArguments;
	}

	@Override
	public IClassBaked getParent() {
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
	public Element getElement() {
		return element;
	}
}
