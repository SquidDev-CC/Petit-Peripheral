package org.squiddev.petit.conversion.from;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.Collection;

public class PrimitiveTypeConverter extends InstanceofConverter {
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
		public Segment validate(String from, String temp) {
			return new Segment("$N instanceof Number", from);
		}

		@Override
		public Segment getValue(String from, String temp) {
			return new Segment("((Number)($N))." + primitive.toString() + "Value()", from);
		}
	}

	public static class CharConverter extends PrimitiveTypeConverter {

		public CharConverter(Environment env) {
			super(env, TypeKind.CHAR, "char");
		}

		@Override
		public Segment validate(String from, String temp) {
			return new Segment("$N instanceof String && ((String)$N).length() == 1", from, from);
		}

		@Override
		public Segment getValue(String from, String temp) {
			return new Segment("((String)$N).charAt(0)", from);
		}
	}
}
