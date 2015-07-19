package org.squiddev.petit.conversion;

import org.squiddev.petit.conversion.from.FromLuaConverter;
import org.squiddev.petit.conversion.from.InstanceofConverter;
import org.squiddev.petit.conversion.to.SimpleConverter;
import org.squiddev.petit.processor.Segment;

public final class DefaultConverters {
	private DefaultConverters() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Converters> T add(T converter) {
		converter.addFromConverter(Object.class, new FromLuaConverter() {
			@Override
			public Segment convertFrom(String fromToken, String toToken) {
				return new Segment("($N = $N) != null", toToken, fromToken);
			}

			@Override
			public String getName() {
				return "anything";
			}
		});

		converter.addFromConverter(int.class, new InstanceofConverter(int.class, "number"));
		converter.addFromConverter(float.class, new InstanceofConverter(float.class, "number"));
		converter.addFromConverter(double.class, new InstanceofConverter(double.class, "number"));
		converter.addFromConverter(byte.class, new InstanceofConverter(byte.class, "number"));
		converter.addFromConverter(String.class, new InstanceofConverter(String.class, "string"));

		converter.addToConverter(byte.class, SimpleConverter.instance);
		converter.addToConverter(short.class, SimpleConverter.instance);
		converter.addToConverter(int.class, SimpleConverter.instance);
		converter.addToConverter(long.class, SimpleConverter.instance);
		converter.addToConverter(float.class, SimpleConverter.instance);
		converter.addToConverter(double.class, SimpleConverter.instance);
		converter.addToConverter(String.class, SimpleConverter.instance);
		converter.addToConverter(Object.class, SimpleConverter.instance);

		return converter;
	}
}
