package org.squiddev.petit.conversion;

import org.squiddev.petit.conversion.from.FromLuaConverter;
import org.squiddev.petit.conversion.to.ToLuaConverter;
import org.squiddev.petit.processor.Segment;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry of converters
 */
public class Converters {
	protected final Map<Class<?>, FromLuaConverter> fromConverters = new HashMap<Class<?>, FromLuaConverter>();
	protected final Map<Class<?>, ToLuaConverter> toConverters = new HashMap<Class<?>, ToLuaConverter>();

	public void addFromConverter(Class<?> type, FromLuaConverter converter) {
		if (fromConverters.containsKey(type)) {
			throw new IllegalArgumentException("Cannot override " + type);
		}
		fromConverters.put(type, converter);
	}

	public void addToConverter(Class<?> type, ToLuaConverter converter) {
		if (toConverters.containsKey(type)) {
			throw new IllegalArgumentException("Cannot override " + type);
		}
		toConverters.put(type, converter);
	}

	public Segment convertFrom(Class<?> type, String from, String to) {
		FromLuaConverter converter = getFromConverter(type);
		if (converter == null) throw new IllegalArgumentException("Cannot convert to " + type);
		return converter.convertFrom(from, to);
	}

	public FromLuaConverter getFromConverter(Class<?> type) {
		return fromConverters.get(type);
	}

	public Segment convertTo(Class<?> type, String from, String to) {
		ToLuaConverter converter = getToConverter(type);
		if (converter == null) throw new IllegalArgumentException("Cannot convert from " + type);
		return converter.convertTo(from, to);
	}

	public ToLuaConverter getToConverter(Class<?> type) {
		return toConverters.get(type);
	}
}
