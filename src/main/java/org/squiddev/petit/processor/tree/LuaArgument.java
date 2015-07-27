package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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

		if (getArgumentType() == ArgumentType.VARIABLE && getElement().asType().getKind() != TypeKind.ARRAY) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Expected array for varargs", getElement());
		} else if (getConverter() == null) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Unknown converter for " + getElement().asType(), getElement());
			return false;
		}

		return true;
	}

	@Override
	public FromLuaConverter getConverter() {
		TypeMirror type = getElement().asType();

		// Variable types should have a converter for every argument.
		if (getArgumentType() == ArgumentType.VARIABLE) {
			type = ((ArrayType) type).getComponentType();
		}

		return getEnvironment().getConverter().getFromConverter(type);
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
