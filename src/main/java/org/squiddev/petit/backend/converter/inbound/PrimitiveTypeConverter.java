package org.squiddev.petit.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.backend.Utils;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PrimitiveTypeConverter extends InstanceOfConverter {
	protected final PrimitiveType primitive;
	protected final Collection<TypeMirror> types;

	public PrimitiveTypeConverter(Environment env, TypeKind primitive, String name) {
		this(env, env.getTypeUtils().getPrimitiveType(primitive), name);
	}

	public PrimitiveTypeConverter(Environment env, PrimitiveType primitive, String name) {
		super(env, env.getTypeUtils().boxedClass(primitive).asType(), name);
		this.primitive = primitive;
		this.types = Arrays.asList(primitive, type);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return types;
	}

	public static class NumberConverter extends PrimitiveTypeConverter {
		public NumberConverter(Environment env, PrimitiveType type) {
			super(env, type, "number");
		}

		public NumberConverter(Environment env, TypeKind type) {
			this(env, env.getTypeUtils().getPrimitiveType(type));
		}

		@Override
		public Segment validate(ArgumentBaked argument, String from) {
			return new Segment("$N instanceof Number", from);
		}

		@Override
		public CodeBlock convert(ArgumentBaked argument, String from) {
			return Utils.block("((Number)($N))." + primitive.toString() + "Value()", from);
		}
	}

	public static class CharConverter extends PrimitiveTypeConverter {

		public CharConverter(Environment env) {
			super(env, TypeKind.CHAR, "char");
		}

		@Override
		public Segment validate(ArgumentBaked argument, String from) {
			return new Segment("$N instanceof String && ((String)$N).length() == 1", from, from);
		}

		@Override
		public CodeBlock convert(ArgumentBaked argument, String from) {
			return Utils.block("((String)$N).charAt(0)", from);
		}
	}

	public static Iterable<InboundConverter> add(Environment environment) {
		List<InboundConverter> converters = new ArrayList<InboundConverter>();

		for (TypeKind type : new TypeKind[]{TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.FLOAT, TypeKind.DOUBLE}) {
			converters.add(new PrimitiveTypeConverter.NumberConverter(environment, type));
		}
		converters.add(new PrimitiveTypeConverter(environment, TypeKind.BOOLEAN, "boolean"));
		converters.add(new PrimitiveTypeConverter.CharConverter(environment));

		converters.add(new InstanceOfConverter(environment, String.class, "string"));

		return converters;
	}
}
