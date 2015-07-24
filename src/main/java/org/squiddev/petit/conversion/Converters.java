package org.squiddev.petit.conversion;

import org.squiddev.petit.api.compile.converter.ConverterContainer;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;
import org.squiddev.petit.api.compile.converter.ToLuaConverter;

import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

public class Converters implements ConverterContainer {
	protected final Set<FromLuaConverter> fromConverters = new HashSet<FromLuaConverter>();
	protected final Set<ToLuaConverter> toConverters = new HashSet<ToLuaConverter>();

	@Override
	public void addFromConverter(FromLuaConverter converter) {
		fromConverters.add(converter);
	}

	@Override
	public void addToConverter(ToLuaConverter converter) {
		toConverters.add(converter);
	}

	@Override
	public FromLuaConverter getFromConverter(TypeMirror type) {
		for (FromLuaConverter converter : fromConverters) {
			if (converter.matches(type)) return converter;
		}
		return null;
	}

	@Override
	public ToLuaConverter getToConverter(TypeMirror type) {
		for (ToLuaConverter converter : toConverters) {
			if (converter.matches(type)) return converter;
		}
		return null;
	}
}
