package org.squiddev.petit.conversion;

import org.squiddev.petit.conversion.from.FromLuaConverter;
import org.squiddev.petit.conversion.to.ToLuaConverter;

import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

/**
 * A registry of converters
 */
public class Converters {
	protected final Set<FromLuaConverter> fromConverters = new HashSet<FromLuaConverter>();
	protected final Set<ToLuaConverter> toConverters = new HashSet<ToLuaConverter>();

	public void addFromConverter(FromLuaConverter converter) {
		fromConverters.add(converter);
	}

	public void addToConverter(ToLuaConverter converter) {
		toConverters.add(converter);
	}

	public FromLuaConverter getFromConverter(TypeMirror type) {
		for (FromLuaConverter converter : fromConverters) {
			if (converter.matches(type)) return converter;
		}
		return null;
	}

	public ToLuaConverter getToConverter(TypeMirror type) {
		for (ToLuaConverter converter : toConverters) {
			if (converter.matches(type)) return converter;
		}
		return null;
	}
}
