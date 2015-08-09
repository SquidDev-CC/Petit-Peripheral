package org.squiddev.petit.base.tree.builder;

import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class MethodBuilder implements IMethodBuilder {
	private final IClassBuilder klass;
	private final ExecutableElement method;
	private boolean returnsVarags;
	private String errorMessage;
	private final List<IArgumentBuilder> arguments;
	private final List<String> names = new ArrayList<String>();

	public MethodBuilder(IClassBuilder klass, ExecutableElement method) {
		this.klass = klass;
		this.method = method;

		// Create a list of arguments
		List<? extends VariableElement> params = method.getParameters();
		int size = params.size();
		IArgumentBuilder[] arguments = new IArgumentBuilder[size];
		for (int i = 0; i < size; i++) {
			arguments[i] = new ArgumentBuilder(this, params.get(i), i == size - 1 && method.isVarArgs() ? ArgumentKind.VARIABLE : ArgumentKind.REQUIRED);
		}
		this.arguments = Arrays.asList(arguments);
	}

	public MethodBuilder(IClassBuilder klass) {
		this.klass = klass;
		this.method = null;
		this.arguments = new ArrayList<IArgumentBuilder>();
	}

	protected List<IArgumentBuilder> arguments() {
		return arguments;
	}

	@Override
	public Collection<String> names() {
		return names;
	}

	@Override
	public List<IArgumentBuilder> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	@Override
	public IClassBuilder getParent() {
		return klass;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	@Override
	public boolean getVarReturn() {
		return returnsVarags;
	}

	@Override
	public void setVarReturn(boolean varReturn) {
		returnsVarags = varReturn;
	}

	@Override
	public TypeMirror getReturnType() {
		return ((ExecutableElement) getElement()).getReturnType();
	}

	@Override
	public String getTarget() {
		return Backend.FIELD_INSTANCE + "." + getElement().getSimpleName();
	}

	@Override
	public Element getElement() {
		return method;
	}

	@Override
	public String toString() {
		String args = arguments.toString();
		return method.getSimpleName().toString() + "(" + args.substring(1, args.length() - 1) + "):" + method.getReturnType();
	}
}
