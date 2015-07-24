package org.squiddev.petit.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;
import org.squiddev.petit.conversion.from.FromLuaConverter;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

public class Writer {
	public TypeSpec.Builder writeClass(PeripheralClass klass) {
		TypeSpec.Builder spec = TypeSpec.classBuilder(klass.getGeneratedName())
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
			.addSuperinterface(IPeripheral.class)
			.addMethod(writeName(klass))
			.addMethod(writeEquals(klass))
			.addMethod(writeMethodNames(klass))
			.addMethod(writeCallMethod(klass))
			.addField(TypeName.get(klass.getElement().asType()), "instance", Modifier.PRIVATE);

		// These should do something. First build and all that though.
		spec.addMethod(
			MethodSpec.methodBuilder("attach")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(IComputerAccess.class, "comp")
				.returns(void.class)
				.build()
		);
		spec.addMethod(
			MethodSpec.methodBuilder("detach")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(IComputerAccess.class, "comp")
				.returns(void.class)
				.build()
		);

		spec.addMethod(
			MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(TypeName.get(klass.getElement().asType()), "instance")
				.addStatement("this.instance = instance")
				.build()
		);

		return spec;
	}

	public static class ArgumentMeta {
		public final FromLuaConverter converter;
		public final Argument argument;
		public final String name;

		public boolean required;

		public ArgumentMeta(Argument argument) {
			this.argument = argument;
			FromLuaConverter converter = this.converter = argument.getConverter();
			if (converter.requiresVariable()) {
				name = argument.getMethod().getElement().getSimpleName() + "_" + argument.getElement().getSimpleName();
			} else {
				name = null;
			}

			required = argument.getArgumentType() == ArgumentType.REQUIRED;
		}

		public ArgumentMeta() {
			argument = null;
			name = null;
			required = false;
			converter = null;
		}
	}

	public void writeMethod(MethodSpec.Builder spec, PeripheralMethod method) {
		StringBuilder errorMessage = new StringBuilder("Expected ");

		ArgumentMeta[] arguments = new ArgumentMeta[method.getArguments().size()];
		if (method.getArguments().size() == 1 && method.getArguments().get(0).getArgumentType() == ArgumentType.VARIABLE && method.getEnvironment().getTypeHelpers().isObjectArray(method.getArguments().get(0).getElement().asType())) {
			arguments[0] = new ArgumentMeta();
		} else {
			int requiredLength = 0;

			int i = 0;
			for (Argument argument : method.getArguments()) {
				ArgumentMeta meta = arguments[i] = new ArgumentMeta(argument);

				if (meta.name != null) {
					spec.addStatement("$T $N", argument.getElement().asType(), meta.name);
				}
				if (meta.required) {
					errorMessage.append(argument.getConverter().getName()).append(", ");
					requiredLength++;
				}

				i++;
			}

			i = 0;
			if (requiredLength > 0) {
				spec.addCode("if(args.length < $L", requiredLength);
				for (ArgumentMeta argument : arguments) {
					if (argument.required) {
						Segment segment = argument.converter.validate("args[" + i + "]", argument.name);
						if (segment != null) spec.addCode("|| !(" + segment.contents + ")", segment.values);
					}

					i++;
				}

				spec.beginControlFlow(")");
				String message = method.getErrorMessage();
				if (message == null) {
					message = errorMessage.toString();
					message = message.substring(0, message.length() - 2);
				}
				spec.addStatement("throw new $T($S)", LuaException.class, message);
				spec.endControlFlow();
			}
		}


		spec.addCode("$[");
		if (method.getElement().getReturnType().getKind() != TypeKind.VOID) {
			spec.addCode("$T $N = ", method.getElement().getReturnType(), "funcResult");
		}

		spec.addCode("instance.$N(", method.getElement().getSimpleName());
		int i = 0;
		for (ArgumentMeta argument : arguments) {
			if (i > 0) {
				spec.addCode(", ");
			}

			if (argument.argument == null || argument.argument.getArgumentType() == ArgumentType.VARIABLE) {
				spec.addCode("args");
			} else {
				Segment segment;

				if (argument.converter == null || (segment = argument.converter.getValue("args[" + i + "]", argument.name)) == null) {
					spec.addCode("args[" + i + "]");
				} else {
					spec.addCode("(" + segment.contents + ")", segment.values);
				}
			}

			i++;
		}
		spec.addCode(");$]\n");

		if (method.getElement().getReturnType().getKind() != TypeKind.VOID) {
			Segment segment = method.getConverter().convertTo("funcResult");
			if (segment == null) {
				spec.addStatement("return funcResult");
			} else {
				spec.addStatement("return " + segment.contents, segment.values);
			}
		} else {
			spec.addStatement("return null");
		}
	}

	//region IPeripheral functions
	public MethodSpec writeMethodNames(PeripheralClass klass) {
		MethodSpec.Builder spec = MethodSpec.methodBuilder("getMethodNames")
			.addModifiers(Modifier.PUBLIC)
			.returns(String[].class);


		spec.addCode("$[");
		spec.addCode("return new String[]{");
		for (PeripheralMethod method : klass.methods()) {
			for (String name : method.names()) {
				spec.addCode("$S, ", name);
			}
		}
		spec.addCode("}");
		spec.addCode(";\n$]");
		return spec.build();
	}

	public MethodSpec writeEquals(PeripheralClass klass) {
		String name = klass.getGeneratedName();

		return MethodSpec.methodBuilder("equals")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IPeripheral.class, "other")
			.returns(boolean.class)
			.addStatement("return other instanceof " + name + " && ((" + name + ")other).instance.equals(instance)")
			.build();
	}

	public MethodSpec writeName(PeripheralClass klass) {
		return MethodSpec.methodBuilder("getType")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addStatement("return $S", klass.getName())
			.build();
	}

	public MethodSpec writeCallMethod(PeripheralClass klass) {
		MethodSpec.Builder spec = MethodSpec.methodBuilder("callMethod")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IComputerAccess.class, "computer")
			.addParameter(ILuaContext.class, "context")
			.addParameter(int.class, "index")
			.addParameter(Object[].class, "args")
			.returns(Object[].class)
			.addException(LuaException.class)
			.addException(InterruptedException.class);

		spec.beginControlFlow("switch(index)");

		int i = 0;
		for (PeripheralMethod method : klass.methods()) {
			int end = i + method.names().size();
			for (; i < end; i++) {
				spec.addCode("case $L:", i);
			}
			spec.beginControlFlow("");
			writeMethod(spec, method);
			spec.endControlFlow();
		}

		spec.endControlFlow();
		spec.addStatement("return null");

		return spec.build();
	}
	//endregion
}
