package org.squiddev.petit.api.compile.converter;

import javax.lang.model.type.TypeMirror;

/**
 * A registry of converters
 */
public interface ConverterContainer {
	void addFromConverter(FromLuaConverter converter);

	void addToConverter(ToLuaConverter converter);

	FromLuaConverter getFromConverter(TypeMirror type);

	ToLuaConverter getToConverter(TypeMirror type);
}
