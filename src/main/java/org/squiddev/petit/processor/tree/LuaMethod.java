package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.conversion.to.ToLuaConverter;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LuaMethod {
	/**
	 * The parent class for this method
	 */
	public final LuaClass klass;

	/**
	 * The actual method this will call
	 */
	public final ExecutableElement method;

	/**
	 * If this method returns a varargs
	 */
	public boolean returnsVarags;

	/**
	 * The error message this function should produce
	 * Null if it should be generated automatically
	 */
	public String errorMessage;

	/**
	 * The arguments this function takes
	 */
	public final LuaArgument[] arguments;

	/**
	 * The names used to call this function
	 */
	public final Set<String> names = new HashSet<String>();

	public LuaMethod(LuaClass klass, ExecutableElement method) {
		this.klass = klass;
		this.method = method;

		LuaFunction function = method.getAnnotation(LuaFunction.class);

		// Get default isVarArgs
		this.returnsVarags = function.isVarReturn();

		// Get default error message
		String errorMessage = function.error();
		this.errorMessage = errorMessage != null && errorMessage.isEmpty() ? null : errorMessage;

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
		LuaArgument[] arguments = this.arguments = new LuaArgument[size];
		for (int i = 0; i < size; i++) {
			arguments[i] = new LuaArgument(this, params.get(i), i == size - 1 && method.isVarArgs() ? LuaArgument.KIND_VARARG : LuaArgument.KIND_REQUIRED);
		}

		klass.environment.transformer.transform(this);
	}

	public ToLuaConverter converter() {
		return klass.environment.converters.getToConverter(method.getReturnType());
	}

	public boolean process() {
		boolean success = true;
		Messager messager = klass.environment.processingEnvironment.getMessager();

		for (String name : names) {
			if (name.matches("^[a-zA-Z][a-z0-9A-Z]$")) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Invalid name '" + name + "'", method);
				success = false;
			}
		}

		Types types = klass.environment.processingEnvironment.getTypeUtils();
		if (!types.isSameType(types.getPrimitiveType(TypeKind.VOID), method.getReturnType()) && converter() == null) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Unknown converter for '" + method.getReturnType() + "'", method);
		}

		int state = LuaArgument.KIND_REQUIRED;
		for (LuaArgument argument : arguments) {
			switch (argument.kind) {
				case LuaArgument.KIND_REQUIRED:
					if (state != LuaArgument.KIND_REQUIRED) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected required argument after optional one", argument.parameter);
						success = false;
					}
				case LuaArgument.KIND_OPTIONAL:
					if (state == LuaArgument.KIND_VARARG) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected optional argument after varargs", argument.parameter);
						success = false;
					}
				case LuaArgument.KIND_VARARG:
					break;
				default:
					messager.printMessage(Diagnostic.Kind.WARNING, "Unknown variable kind " + argument.kind + ", this is an internal error", argument.parameter);
			}

			state = argument.kind;

			success &= argument.process();
		}

		return success;
	}
}
