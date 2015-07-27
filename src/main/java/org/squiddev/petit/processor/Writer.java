package org.squiddev.petit.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Segment;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;
import org.squiddev.petit.api.compile.writer.PeripheralWriter;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.List;

public class Writer implements PeripheralWriter {
	public static final String FIELD_INSTANCE = "instance";

	@Override
	public TypeSpec.Builder writeClass(PeripheralClass klass) {
		TypeSpec.Builder spec = TypeSpec.classBuilder(klass.getGeneratedName())
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
			.addSuperinterface(IPeripheral.class)
			.addMethod(writeType(klass))
			.addMethod(writeEquals(klass))
			.addMethod(writeMethodNames(klass))
			.addMethod(writeCall(klass))
			.addField(TypeName.get(klass.getElement().asType()), FIELD_INSTANCE, Modifier.PRIVATE);

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
				.addStatement("this.$N = instance", FIELD_INSTANCE)
				.build()
		);

		return spec;
	}

	/**
	 * Caches basic parts of the argument
	 */
	public static class ArgumentMeta {
		public final FromLuaConverter converter;
		public final Argument argument;

		public ArgumentMeta(Argument argument) {
			this.argument = argument;
			this.converter = argument.getConverter();
		}

		public boolean isTrivial() {
			return argument.getArgumentType() == ArgumentType.VARIABLE && argument.getEnvironment().getTypeHelpers().isObjectArray(argument.getElement().asType());
		}
	}

	@Override
	public CodeBlock writeMethod(PeripheralMethod method) {
		CodeBlock.Builder spec = CodeBlock.builder();
		StringBuilder errorMessage = new StringBuilder("Expected ");

		List<ArgumentMeta> arguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		List<ArgumentMeta> actualArguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		int requiredLength = 0;

		for (Argument argument : method.getArguments()) {
			ArgumentMeta meta = new ArgumentMeta(argument);
			arguments.add(meta);

			if (argument.getArgumentType() == ArgumentType.REQUIRED) {
				errorMessage.append(argument.getConverter().getName()).append(", ");
				requiredLength++;
			}

			if (argument.getArgumentType() != ArgumentType.PROVIDED) {
				actualArguments.add(meta);
			}

			CodeBlock block = meta.converter.preamble(argument);
			if (block != null) spec.add(block);
		}

		int argIndex = 0;
		String message = method.getErrorMessage();
		if (message == null) {
			message = errorMessage.toString();
			message = message.substring(0, message.length() - 2);
		}

		boolean expression = false;
		if (requiredLength > 0) {
			spec.add("if((args.length < $L", requiredLength);
			expression = true;
		}

		for (ArgumentMeta argument : arguments) {
			Segment segment = null;
			switch (argument.argument.getArgumentType()) {
				case REQUIRED:
					segment = argument.converter.validate(argument.argument, ARG_ARGS + "[" + argIndex + "]");

					argIndex++;
					break;
				case OPTIONAL:
					segment = argument.converter.validate(argument.argument, ARG_ARGS + "[" + argIndex + "]");
					if (segment != null) {
						if (argIndex > 0) {
							if (segment.isStatement()) {
								segment = new Segment(CodeBlock.builder()
									.beginControlFlow("if($N.length >= $L", ARG_ARGS, argIndex + 1)
									.add(segment.getCodeBlock())
									.endControlFlow()
									.build(), true);
							} else {
								segment = new Segment(CodeBlock.builder()
									.add("$N.length >= $L && ", ARG_ARGS, argIndex + 1)
									.add(segment.getCodeBlock())
									.build(), false);
							}
						}
					}

					argIndex++;
					break;
				case PROVIDED:
					segment = argument.converter.validate(argument.argument, null);
					break;
				case VARIABLE:
					// TODO
					break;
			}

			if (segment != null) {
				if (segment.isStatement()) {
					if (expression) {
						spec.beginControlFlow("))");
						spec.addStatement("throw new $T($S)", LuaException.class, message);
						spec.endControlFlow();
					}

					expression = false;
					spec.add(segment.getCodeBlock());
				} else {
					if (expression) {
						spec.add(") || !(");
						spec.add(segment.getCodeBlock());
					} else {
						spec.add("if(!(");
						spec.add(segment.getCodeBlock());
					}

					expression = true;
				}
			}
		}

		if (expression) {
			spec.beginControlFlow("))");
			spec.addStatement("throw new $T($S)", LuaException.class, message);
			spec.endControlFlow();
		}


		spec.add("$[");
		if (method.getElement().getReturnType().getKind() != TypeKind.VOID) {
			spec.add("$T $N = ", method.getElement().getReturnType(), "funcResult");
		}

		spec.add("$N.$N(", FIELD_INSTANCE, method.getElement().getSimpleName());
		boolean first = true;
		int arrayIndex = 0;

		for (ArgumentMeta argument : arguments) {
			if (first) {
				first = false;
			} else {
				spec.add(", ");
			}

			if (actualArguments.size() == 0 && actualArguments.get(0).isTrivial()) {
				spec.add("args");
			} else {
				CodeBlock block = null;

				switch (argument.argument.getArgumentType()) {
					case REQUIRED:
						block = argument.converter.convert(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
						if (block == null) block = Utils.block(ARG_ARGS + "[" + arrayIndex + "]");

						argIndex++;
						break;
					case OPTIONAL:
						block = argument.converter.convert(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
						if (block == null) block = Utils.block(ARG_ARGS + "[" + arrayIndex + "]");

						block = CodeBlock.builder()
							.add("$N.length >= $L ? (", ARG_ARGS, argIndex + 1)
							.add(block)
							.add(") : null")
							.build();

						argIndex++;
						break;
					case PROVIDED:
						block = argument.converter.convert(argument.argument, null);
						if (block == null) block = Utils.block("null");
						break;
					case VARIABLE:
						// TODO
						break;
				}

				if (block == null) block = Utils.block("null");

				spec.add("(");
				spec.add(block);
				spec.add(")");
			}
		}
		spec.add(");$]\n");

		if (method.getElement().getReturnType().getKind() != TypeKind.VOID) {
			CodeBlock block = method.getConverter().convertTo("funcResult");
			if (block == null) {
				spec.addStatement("return funcResult");
			} else {
				spec.add("$[return ");
				spec.add(block);
				spec.add(";\n$]");
			}
		} else {
			spec.addStatement("return null");
		}

		return spec.build();
	}

	//region IPeripheral functions
	@Override
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

	@Override
	public MethodSpec writeEquals(PeripheralClass klass) {
		String name = klass.getGeneratedName();

		return MethodSpec.methodBuilder("equals")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IPeripheral.class, "other")
			.returns(boolean.class)
			.addStatement("return other instanceof " + name + " && ((" + name + ")other).$N.equals($N)", FIELD_INSTANCE, FIELD_INSTANCE)
			.build();
	}

	@Override
	public MethodSpec writeType(PeripheralClass klass) {
		return MethodSpec.methodBuilder("getType")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addStatement("return $S", klass.getName())
			.build();
	}

	@Override
	public MethodSpec writeCall(PeripheralClass klass) {
		MethodSpec.Builder spec = MethodSpec.methodBuilder("callMethod")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IComputerAccess.class, ARG_COMPUTER)
			.addParameter(ILuaContext.class, ARG_LUA_CONTEXT)
			.addParameter(int.class, "index")
			.addParameter(Object[].class, ARG_ARGS)
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
			spec.addCode(writeMethod(method));
			spec.endControlFlow();
		}

		spec.endControlFlow();
		spec.addStatement("return null");

		return spec.build();
	}
	//endregion
}
