package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.ToLuaConverter;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

public class LuaMethod implements PeripheralMethod {
	private final PeripheralClass klass;
	private final ExecutableElement method;
	private boolean returnsVarags;
	private String errorMessage;
	private final List<Argument> arguments;
	private final List<String> names = new ArrayList<String>();

	public LuaMethod(PeripheralClass klass, ExecutableElement method) {
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
		Argument[] arguments = new Argument[size];
		for (int i = 0; i < size; i++) {
			arguments[i] = new LuaArgument(this, params.get(i), i == size - 1 && method.isVarArgs() ? ArgumentType.VARIABLE : ArgumentType.REQUIRED);
		}
		this.arguments = Collections.unmodifiableList(Arrays.asList(arguments));

		getEnvironment().getTransformer().transform(this);
	}

	@Override
	public ToLuaConverter getConverter() {
		return getEnvironment().getConverter().getToConverter(method.getReturnType());
	}

	@Override
	public boolean process() {
		boolean success = true;
		Messager messager = getEnvironment().getMessager();

		for (String name : names()) {
			if (name.matches("^[a-zA-Z][a-z0-9A-Z]$")) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Invalid name '" + name + "'", method);
				success = false;
			}
		}

		Types types = getEnvironment().getTypeUtils();
		if (!types.isSameType(types.getNoType(TypeKind.VOID), method.getReturnType()) && getConverter() == null) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Unknown converter for '" + method.getReturnType() + "'", method);
		}

		ArgumentType state = ArgumentType.REQUIRED;
		for (Argument argument : getArguments()) {
			switch (argument.getArgumentType()) {
				case REQUIRED:
					if (state != ArgumentType.REQUIRED) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected required argument after optional one", argument.getElement());
						success = false;
					}
					state = argument.getArgumentType();
					break;
				case OPTIONAL:
					if (state == ArgumentType.VARIABLE) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected optional argument after varargs", argument.getElement());
						success = false;
					}
					state = argument.getArgumentType();
					break;
				case VARIABLE:
					state = argument.getArgumentType();
					break;
				case PROVIDED:
					break;
				default:
					messager.printMessage(Diagnostic.Kind.WARNING, "Unknown variable kind " + argument.getArgumentType() + ", this is an internal error", argument.getElement());
			}

			success &= argument.process();
		}

		return success;
	}

	@Override
	public Collection<String> names() {
		return names;
	}

	@Override
	public List<Argument> getArguments() {
		return arguments;
	}

	@Override
	public PeripheralClass getPeripheral() {
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
	public Environment getEnvironment() {
		return getPeripheral().getEnvironment();
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
