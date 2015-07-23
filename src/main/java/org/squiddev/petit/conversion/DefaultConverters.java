package org.squiddev.petit.conversion;

import org.squiddev.petit.conversion.from.AbstractFromLuaConverter;
import org.squiddev.petit.conversion.from.InstanceofConverter;
import org.squiddev.petit.conversion.from.NumberConverter;
import org.squiddev.petit.conversion.to.SimpleConverter;
import org.squiddev.petit.processor.Environment;
import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;

public final class DefaultConverters {
	private DefaultConverters() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Environment> T add(T env) {
		Converters converter = env.converters;
		converter.addFromConverter(new AbstractFromLuaConverter(env) {
			@Override
			public Iterable<TypeMirror> getTypes() {
				return Collections.singleton(environment.typeHelpers.getMirror(Object.class));
			}

			@Override
			public Segment validate(String from, String temp) {
				return null;
			}

			@Override
			public Segment getValue(String from, String temp) {
				return null;
			}

			@Override
			public String getName() {
				return "anything";
			}
		});

		converter.addFromConverter(new NumberConverter(env, TypeKind.BYTE));
		converter.addFromConverter(new NumberConverter(env, TypeKind.SHORT));
		converter.addFromConverter(new NumberConverter(env, TypeKind.INT));
		converter.addFromConverter(new NumberConverter(env, TypeKind.LONG));
		converter.addFromConverter(new NumberConverter(env, TypeKind.FLOAT));
		converter.addFromConverter(new NumberConverter(env, TypeKind.DOUBLE));
		converter.addFromConverter(new InstanceofConverter(env, String.class, "string"));

		converter.addToConverter(new SimpleConverter(env, byte.class));
		converter.addToConverter(new SimpleConverter(env, short.class));
		converter.addToConverter(new SimpleConverter(env, int.class));
		converter.addToConverter(new SimpleConverter(env, long.class));
		converter.addToConverter(new SimpleConverter(env, float.class));
		converter.addToConverter(new SimpleConverter(env, double.class));
		converter.addToConverter(new SimpleConverter(env, String.class));
		converter.addToConverter(new SimpleConverter(env, Object.class));

		return env;
	}
}
