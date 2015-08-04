package org.squiddev.petit.backend.iperipheral;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.tree.ArgumentKind;
import org.squiddev.petit.api.compile.tree.MethodSignature;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;
import org.squiddev.petit.backend.AbstractBackend;
import org.squiddev.petit.backend.Utils;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class IPeripheralWriter extends AbstractBackend {
	public final String ARG_COMPUTER = "computer";
	public final String ARG_LUA_CONTEXT = "luaContext";
	public final String FIELD_INSTANCE = "instance";
	public final String ARG_ARGS = "args";
	public final String VAR_REST = "rest";

	protected final Environment environment;

	public IPeripheralWriter(Environment environment) {
		this.environment = environment;
	}

	@Override
	public TypeSpec.Builder writeClass(ClassBaked baked) {
		TypeSpec.Builder spec = TypeSpec.classBuilder(baked.getGeneratedName())
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
			.addSuperinterface(IPeripheral.class)
			.addMethod(writeType(baked))
			.addMethod(writeEquals(baked))
			.addMethod(writeMethodNames(baked))
			.addMethod(writeCall(baked))
			.addField(TypeName.get(baked.getElement().asType()), FIELD_INSTANCE, Modifier.PRIVATE);

		// These should do something. First build and all that though.
		spec.addMethod(
			MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(TypeName.get(baked.getElement().asType()), "instance")
				.addStatement("this.$N = instance", FIELD_INSTANCE)
				.build()
		);

		for (Map.Entry<MethodSignature, Collection<SyntheticMethod>> synthetic : baked.getSyntheticMethods().entrySet()) {
			MethodSpec.Builder builder = MethodSpec.methodBuilder(synthetic.getKey().getName())
				.addModifiers(Modifier.PUBLIC);

			int i = 0;
			for (TypeMirror type : synthetic.getKey().getParameters()) {
				builder.addParameter(TypeName.get(type), "arg_" + i);
			}
			for (SyntheticMethod method : synthetic.getValue()) {
				builder.returns(TypeName.get(method.getReturnType()));
				builder.addCode(method.build());
			}

			spec.addMethod(builder.build());
		}

		return spec;
	}

	/**
	 * Caches basic parts of the argument
	 */
	public class ArgumentMeta {
		public final InboundConverter converter;
		public final ArgumentBaked argument;

		public ArgumentMeta(ArgumentBaked argument) {
			this.argument = argument;

			TypeMirror type = argument.getElement().asType();
			if (argument.getKind() == ArgumentKind.VARIABLE) {
				type = ((ArrayType) type).getComponentType();
			}
			this.converter = getInboundConverter(argument.getKind(), type);
		}

		public boolean isTrivial() {
			return argument.getKind() == ArgumentKind.VARIABLE && environment.getTypeHelpers().isObjectArray(argument.getElement().asType());
		}
	}

	//region Method segments

	public Segment getValidation(ArgumentMeta argument, int arrayIndex, String exception) {
		switch (argument.argument.getKind()) {
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

				TypeMirror type = argument.argument.getType();
				CodeBlock.Builder builder = CodeBlock.builder()
					.addStatement("$T $N = new $T[$N.length - $L]", environment.getTypeUtils().getArrayType(type), VAR_REST, type, ARG_ARGS, arrayIndex)
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
		switch (argument.argument.getKind()) {
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

	public CodeBlock writeMethod(MethodBaked method) {
		CodeBlock.Builder spec = CodeBlock.builder();
		spec.addStatement("// $L", method.toString());
		StringBuilder errorMessage = new StringBuilder("Expected ");

		List<ArgumentMeta> arguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		List<ArgumentMeta> actualArguments = new ArrayList<ArgumentMeta>(method.getArguments().size());
		int requiredLength = 0;

		for (ArgumentBaked argument : method.getArguments()) {
			ArgumentMeta meta = new ArgumentMeta(argument);
			arguments.add(meta);

			InboundConverter converter = meta.converter;
			switch (argument.getKind()) {
				case REQUIRED:
					errorMessage.append(converter.getName()).append(", ");
					actualArguments.add(meta);
					requiredLength++;
					break;
				case OPTIONAL:
					actualArguments.add(meta);
					errorMessage.append("[").append(converter.getName()).append("], ");
					break;
				case VARIABLE:
					actualArguments.add(meta);
					errorMessage.append("[").append(converter.getName()).append("...], ");
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
				if (argument.argument.getKind() != ArgumentKind.PROVIDED) arrayIndex++;

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
		if (method.getReturnType().getKind() != TypeKind.VOID) {
			spec.add("$T $N = ", method.getReturnType(), "funcResult");
		}

		spec.add("$N(", method.getTarget());
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
				if (argument.argument.getKind() != ArgumentKind.PROVIDED) arrayIndex++;
			}
		}

		spec.add(");$]\n");

		if (method.getReturnType().getKind() != TypeKind.VOID) {
			CodeBlock block = getToConverter(method.getReturnType()).convertTo("funcResult");
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
	public MethodSpec writeMethodNames(ClassBaked klass) {
		MethodSpec.Builder spec = MethodSpec.methodBuilder("getMethodNames")
			.addModifiers(Modifier.PUBLIC)
			.returns(String[].class);


		spec.addCode("$[");
		spec.addCode("return new String[]{");
		for (MethodBaked method : klass.getMethods()) {
			for (String name : method.getNames()) {
				spec.addCode("$S, ", name);
			}
		}
		spec.addCode("}");
		spec.addCode(";\n$]");
		return spec.build();
	}

	public MethodSpec writeEquals(ClassBaked klass) {
		String name = klass.getGeneratedName();

		return MethodSpec.methodBuilder("equals")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IPeripheral.class, "other")
			.returns(boolean.class)
			.addStatement("return other instanceof " + name + " && ((" + name + ")other).$N.equals($N)", FIELD_INSTANCE, FIELD_INSTANCE)
			.build();
	}

	public MethodSpec writeType(ClassBaked klass) {
		return MethodSpec.methodBuilder("getType")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addStatement("return $S", klass.getName())
			.build();
	}

	public MethodSpec writeCall(ClassBaked klass) {
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
		for (MethodBaked method : klass.getMethods()) {
			int end = i + method.getNames().size();
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
