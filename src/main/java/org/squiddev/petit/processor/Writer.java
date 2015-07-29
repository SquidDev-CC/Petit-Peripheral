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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class Writer implements PeripheralWriter {
	public static final String FIELD_INSTANCE = "instance";

	public static final String VAR_REST = "rest";

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

	//region Method segments

	public Segment getValidation(ArgumentMeta argument, int arrayIndex, String exception) {
		switch (argument.argument.getArgumentType()) {
			case REQUIRED:
				return argument.converter.validate(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
			case OPTIONAL: {
				Segment segment = argument.converter.validate(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
				if (segment != null) {
					if (arrayIndex > 0) {
						if (segment.isStatement()) {
							segment = new Segment(CodeBlock.builder()
								.beginControlFlow("if($N.length >= $L)", ARG_ARGS, arrayIndex + 1)
								.add(segment.getCodeBlock())
								.endControlFlow()
								.build(), true);
						} else {
							segment = new Segment(CodeBlock.builder()
								.add("$N.length < $L || ", ARG_ARGS, arrayIndex + 1)
								.add(segment.getCodeBlock())
								.build(), false);
						}
					}
				}

				return segment;
			}
			case PROVIDED:
				return argument.converter.validate(argument.argument, null);
			case VARIABLE: {
				if (arrayIndex == 0 && argument.isTrivial()) return null;

				TypeMirror type = argument.argument.getElement().asType();
				CodeBlock.Builder builder = CodeBlock.builder()
					.addStatement("$T $N = new $T[$N.length - $L]", type, VAR_REST, ((ArrayType) type).getComponentType(), ARG_ARGS, arrayIndex)
					.beginControlFlow("for(int i = $L; i < $N.length; i++)", arrayIndex, ARG_ARGS);

				Segment validate = argument.converter.validate(argument.argument, ARG_ARGS + "[i]");
				CodeBlock convert = argument.converter.convert(argument.argument, ARG_ARGS + "[i]");

				if (validate != null) {
					if (validate.isStatement()) {
						builder.add(validate.getCodeBlock());
					} else {
						builder
							.add("if(")
							.add(validate.getCodeBlock())
							.beginControlFlow(")")
							.add("$[$N[i - $L] = ", VAR_REST, arrayIndex)
							.add(convert == null ? Utils.block(ARG_ARGS + "[i]") : convert)
							.add(";\n$]")
							.nextControlFlow("else")
							.addStatement("throw new $T($S)", LuaException.class, exception)
							.endControlFlow();
					}
				} else {
					builder
						.add("$[$N[i - $L] = ", VAR_REST, arrayIndex)
						.add(convert == null ? Utils.block(ARG_ARGS + "[i]") : convert)
						.add(";\n$]");
				}

				builder.endControlFlow();

				return new Segment(builder.build(), true);
			}

			default:
				throw new IllegalArgumentException("Unknown type for " + argument.argument);
		}
	}

	public CodeBlock getConverter(ArgumentMeta argument, int arrayIndex) {
		switch (argument.argument.getArgumentType()) {
			case REQUIRED: {
				CodeBlock block = argument.converter.convert(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
				return block == null ? Utils.block(ARG_ARGS + "[" + arrayIndex + "]") : block;
			}
			case OPTIONAL: {
				CodeBlock block = argument.converter.convert(argument.argument, ARG_ARGS + "[" + arrayIndex + "]");
				if (block == null) block = Utils.block(ARG_ARGS + "[" + arrayIndex + "]");

				return CodeBlock.builder()
					.add("$N.length >= $L ? (", ARG_ARGS, arrayIndex + 1)
					.add(block)
					.add(") : null")
					.build();

			}
			case PROVIDED: {
				CodeBlock block = argument.converter.convert(argument.argument, null);
				return block == null ? Utils.block("null") : block;
			}
			case VARIABLE:
				return Utils.block(arrayIndex == 0 && argument.isTrivial() ? ARG_ARGS : VAR_REST);

			default:
				throw new IllegalArgumentException("Unknown type for " + argument.argument);
		}
	}

	@Override
	public CodeBlock writeMethod(PeripheralMethod method) {
		CodeBlock.Builder spec = CodeBlock.builder();
		spec.addStatement("// $L", method.toString());
		StringBuilder errorMessage = new StringBuilder("Expected ");

		List<ArgumentMeta> arguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		List<ArgumentMeta> actualArguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		int requiredLength = 0;

		for (Argument argument : method.getArguments()) {
			ArgumentMeta meta = new ArgumentMeta(argument);
			arguments.add(meta);

			switch (argument.getArgumentType()) {
				case REQUIRED:
					errorMessage.append(argument.getConverter().getName()).append(", ");
					actualArguments.add(meta);
					requiredLength++;
					break;
				case OPTIONAL:
					actualArguments.add(meta);
					errorMessage.append("[").append(argument.getConverter().getName()).append("], ");
					break;
				case VARIABLE:
					actualArguments.add(meta);
					errorMessage.append("[").append(argument.getConverter().getName()).append("...], ");
					break;
			}

			CodeBlock block = meta.converter.preamble(argument);
			if (block != null) spec.add(block);
		}

		{
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

			int arrayIndex = 0;
			for (ArgumentMeta argument : arguments) {
				Segment segment = getValidation(argument, arrayIndex, message);
				if (argument.argument.getArgumentType() != ArgumentType.PROVIDED) arrayIndex++;

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

			if (actualArguments.size() == 1 && actualArguments.get(0).isTrivial()) {
				spec.add("args");
			} else {
				spec.add(getConverter(argument, arrayIndex));
				if (argument.argument.getArgumentType() != ArgumentType.PROVIDED) arrayIndex++;
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
	//endregion

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
