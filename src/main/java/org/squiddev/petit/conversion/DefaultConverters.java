package org.squiddev.petit.conversion;

import org.squiddev.petit.conversion.from.AbstractFromLuaConverter;
import org.squiddev.petit.conversion.from.InstanceofConverter;
import org.squiddev.petit.conversion.to.SimpleConverter;
import org.squiddev.petit.processor.Environment;
import org.squiddev.petit.processor.Segment;

public final class DefaultConverters {
	private DefaultConverters() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Environment> T add(T env) {
		Converters converter = env.converters;
		converter.addFromConverter(new AbstractFromLuaConverter(env, Object.class) {
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

		converter.addFromConverter(new InstanceofConverter(env, int.class, "number"));
		converter.addFromConverter(new InstanceofConverter(env, float.class, "number"));
		converter.addFromConverter(new InstanceofConverter(env, double.class, "number"));
		converter.addFromConverter(new InstanceofConverter(env, byte.class, "number"));
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
