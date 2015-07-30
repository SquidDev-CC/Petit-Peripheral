package org.squiddev.petit.transformer.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class BasicMethodBuilder implements MethodBuilder {
	private final ClassBuilder klass;
	private final ExecutableElement method;
	private boolean returnsVarags;
	private String errorMessage;
	private final List<org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder> arguments;
	private final List<String> names = new ArrayList<String>();

	public BasicMethodBuilder(ClassBuilder klass, ExecutableElement method) {
		this.klass = klass;
		this.method = method;

		LuaFunction function = method.getAnnotation(LuaFunction.class);

		// Get default isVarArgs
		this.returnsVarags = function.isVarReturn();

		// Get default error message
		String errorMessage = function.error();
		this.errorMessage = errorMessage == null || errorMessage.isEmpty() ? null : errorMessage;

		// Create the names of this function
		String[] luaName = function.value();
		if (luaName == null || luaName.length == 0 || (luaName.length == 1 && luaName[0].isEmpty())) {
			names.add(method.getSimpleName().toString());
		} else {
			Collections.addAll(names, luaName);
		}

		// Create a list of arguments
		List<? extends VariableElement> params = method.getParameters();
		int size = params.size();
		org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder[] arguments = new org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder[size];
		for (int i = 0; i < size; i++) {
			arguments[i] = new BasicArgumentBuilder(this, params.get(i), i == size - 1 && method.isVarArgs() ? ArgumentType.VARIABLE : ArgumentType.REQUIRED);
		}
		this.arguments = Collections.unmodifiableList(Arrays.asList(arguments));
	}

	@Override
	public Collection<String> names() {
		return names;
	}

	@Override
	public List<org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder> getArguments() {
		return arguments;
	}

	@Override
	public ClassBuilder getParent() {
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
	public ExecutableElement getElement() {
		return method;
	}

	@Override
	public String toString() {
		String args = arguments.toString();
		return method.getSimpleName().toString() + "(" + args.substring(1, args.length() - 1) + "):" + method.getReturnType();
	}
}
