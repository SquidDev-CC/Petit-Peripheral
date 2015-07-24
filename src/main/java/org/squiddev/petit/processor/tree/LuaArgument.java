package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * Stores one argument of a Lua method
 */
public class LuaArgument implements Argument {
	private final PeripheralMethod method;
	private final VariableElement parameter;
	private ArgumentType type = ArgumentType.REQUIRED;

	public LuaArgument(PeripheralMethod method, VariableElement parameter, ArgumentType type) {
		this.parameter = parameter;
		this.method = method;
		this.type = type;

		method.getEnvironment().getTransformer().transform(this);
	}

	@Override
	public boolean process() {
		Environment env = getEnvironment();
		Messager messager = env.getMessager();

		// We handle Object[] specially
		if (!(getArgumentType() == ArgumentType.REQUIRED && env.getTypeHelpers().isObjectArray(getElement().asType())) && getConverter() == null) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Unknown converter for " + getElement().asType(), getElement());
			return false;
		}

		return true;
	}

	@Override
	public FromLuaConverter getConverter() {
		return getEnvironment().getConverter().getFromConverter(parameter.asType());
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
	public PeripheralMethod getMethod() {
		return method;
	}

	@Override
	public Environment getEnvironment() {
		return getMethod().getEnvironment();
	}

	@Override
	public VariableElement getElement() {
		return parameter;
	}
}
