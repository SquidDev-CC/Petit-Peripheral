package org.squiddev.petit.conversion.from;

import org.squiddev.petit.processor.Environment;
import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;

public class NumberConverter extends InstanceofConverter {
	private final TypeMirror boxed;

	public NumberConverter(Environment env, PrimitiveType type) {
		super(env, type, "number");
		this.boxed = env.typeHelpers.boxType(type);
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
		// if (type.getKind() == TypeKind.DOUBLE) return new Segment("(Double)($N)", from);
		return new Segment("((Number)($N))." + type.toString() + "Value()", from);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return Arrays.asList(type, boxed);
	}
}
