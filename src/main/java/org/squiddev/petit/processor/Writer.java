package org.squiddev.petit.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.processor.tree.LuaArgument;
import org.squiddev.petit.processor.tree.LuaClass;
import org.squiddev.petit.processor.tree.LuaMethod;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

public class Writer {
	public TypeSpec.Builder writeClass(LuaClass klass) {
		TypeSpec.Builder spec = TypeSpec.classBuilder(klass.getGeneratedName())
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
			.addSuperinterface(IPeripheral.class)
			.addMethod(writeName(klass))
			.addMethod(writeEquals(klass))
			.addMethod(writeMethodNames(klass))
			.addMethod(writeCallMethod(klass))
			.addField(TypeName.get(klass.klass.asType()), "instance", Modifier.PRIVATE);

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
				.addParameter(TypeName.get(klass.klass.asType()), "instance")
				.addStatement("this.instance = instance")
				.build()
		);

		return spec;
	}

	public void writeMethod(MethodSpec.Builder spec, LuaMethod method) {
		StringBuilder errorMessage = new StringBuilder("Expected ");

		int requiredLength = 0;
		int i = 0;
		for (LuaArgument argument : method.arguments) {
			spec.addStatement("$T $N", argument.parameter.asType(), argument.parameter.getSimpleName().toString() + i);
			if (argument.kind == LuaArgument.KIND_REQUIRED) {
				errorMessage.append(argument.converter().getName()).append(", ");
				requiredLength++;
			}
			i++;
		}

		i = 0;
		if (requiredLength > 0) {
			spec.addCode("if(args.length < $L", requiredLength);
			for (LuaArgument argument : method.arguments) {
				if (argument.kind == LuaArgument.KIND_REQUIRED) {
					Segment segment = argument.converter().convertFrom("args[" + i + "]", argument.parameter.getSimpleName().toString() + i);
					spec.addCode("|| !(" + segment.contents + ")", segment.values);
				} else {
					break;
				}

				i++;
			}
			spec.beginControlFlow(")");
			spec.addStatement("throw new $T($S)", LuaException.class, method.errorMessage == null ? errorMessage.toString() : method.errorMessage);
			spec.endControlFlow();
		}

		for (LuaArgument argument : method.arguments) {
			if (argument.kind > LuaArgument.KIND_REQUIRED) {
				if (argument.kind != LuaArgument.KIND_VARARG || !argument.getType().equals(Object[].class)) {
					Segment segment = argument.converter().convertFrom("args[" + i + "]", argument.parameter.getSimpleName().toString() + i);
					spec.addStatement(segment.contents, segment.values);
				}
			}

			i++;
		}

		spec.addCode("$[");
		if (method.method.getReturnType().getKind() != TypeKind.VOID) {
			spec.addCode("$T $N = ", method.method.getReturnType(), "func_result");
		}
		spec.addCode("instance.$N(", method.method.getSimpleName());

		i = 0;
		for (LuaArgument argument : method.arguments) {
			if (i > 0) {
				spec.addCode(", ");
			}
			spec.addCode(argument.parameter.getSimpleName().toString() + i);
			i++;
		}
		spec.addCode(");$]\n");

		if (method.method.getReturnType().getKind() != TypeKind.VOID) {
			spec.addStatement("Object[] func_return");
			Segment segment = method.converter().convertTo("func_result", "func_return");
			if (segment.contents.startsWith("$[")) {
				spec.addCode(segment.contents, segment.values);
			} else {
				spec.addStatement(segment.contents, segment.values);
			}
			spec.addStatement("return func_return");
		}
	}

	//region IPeripheral functions
	public MethodSpec writeMethodNames(LuaClass klass) {
		MethodSpec.Builder spec = MethodSpec.methodBuilder("getMethodNames")
			.addModifiers(Modifier.PUBLIC)
			.returns(String[].class);


		spec.addCode("$[");
		spec.addCode("return new String[]{");
		for (LuaMethod method : klass.methods) {
			for (String name : method.names) {
				spec.addCode("$S, ", name);
			}
		}
		spec.addCode("}");
		spec.addCode(";\n$]");
		return spec.build();
	}

	public MethodSpec writeEquals(LuaClass klass) {
		String name = klass.getGeneratedName();

		return MethodSpec.methodBuilder("equals")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IPeripheral.class, "other")
			.returns(boolean.class)
			.addStatement("return other instanceof " + name + " && ((" + name + ")other).instance.equals(instance)")
			.build();
	}

	public MethodSpec writeName(LuaClass klass) {
		return MethodSpec.methodBuilder("getType")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addStatement("return $S", klass.name)
			.build();
	}

	public MethodSpec writeCallMethod(LuaClass klass) {
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
		for (LuaMethod method : klass.methods) {
			int end = i + method.names.size();
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
