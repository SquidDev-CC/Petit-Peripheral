package org.squiddev.petit.processor.tree;

import org.squiddev.petit.conversion.from.FromLuaConverter;
import org.squiddev.petit.processor.TypeHelpers;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * Stores one argument of a Lua method
 */
public class LuaArgument {
	public final static int KIND_REQUIRED = 0;
	public final static int KIND_OPTIONAL = 1;
	public final static int KIND_VARARG = 2;

	/**
	 * The parent method for this argument
	 */
	public final LuaMethod method;

	/**
	 * The parameter for this argument
	 */
	public final VariableElement parameter;

	/**
	 * Should this be included in the count of required arguments?
	 */
	public int kind = KIND_REQUIRED;

	private Class<?> type;

	public LuaArgument(LuaMethod method, VariableElement parameter, int kind) {
		this.parameter = parameter;
		this.method = method;
		this.kind = kind;

		method.klass.environment.transformer.transform(this);
	}

	@Override
	public String toString() {
		return "LuaArgument<" + parameter + ">";
	}

	public FromLuaConverter converter() {
		return method.klass.environment.converters.getFromConverter(getType());
	}

	public Class<?> getType() {
		return type;
	}

	public boolean process() {
		Messager messager = method.klass.environment.processingEnvironment.getMessager();

		try {
			type = TypeHelpers.getType(parameter);
		} catch (ClassNotFoundException e) {
			messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), parameter);
			return false;
		}

		// We handle Object[] specially
		if (kind != KIND_VARARG && !type.equals(Object[].class) && converter() == null) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Unknown converter for " + type.toString(), parameter);
			return false;
		}

		return true;
	}
}
