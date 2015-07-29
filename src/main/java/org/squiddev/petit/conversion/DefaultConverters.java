package org.squiddev.petit.conversion;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.ConverterContainer;
import org.squiddev.petit.api.compile.writer.Writer;
import org.squiddev.petit.conversion.from.AbstractFromLuaConverter;
import org.squiddev.petit.conversion.from.InstanceofConverter;
import org.squiddev.petit.conversion.from.PrimitiveTypeConverter;
import org.squiddev.petit.conversion.from.ProvidedConverter;
import org.squiddev.petit.conversion.to.SimpleConverter;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;

public final class DefaultConverters {
	private DefaultConverters() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Environment> T add(T env) {
		ConverterContainer converter = env.getConverter();
		converter.addFromConverter(new AbstractFromLuaConverter(env, "anything") {
			@Override
			public Iterable<TypeMirror> getTypes() {
				return Collections.singleton(environment.getTypeHelpers().object());
			}
		});

		for (TypeKind type : new TypeKind[]{TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.FLOAT, TypeKind.DOUBLE}) {
			converter.addFromConverter(new PrimitiveTypeConverter.NumberConverter(env, type));
		}
		converter.addFromConverter(new PrimitiveTypeConverter(env, TypeKind.BOOLEAN, "boolean"));
		converter.addFromConverter(new PrimitiveTypeConverter.CharConverter(env));
		converter.addFromConverter(new InstanceofConverter(env, String.class, "string"));

		converter.addFromConverter(new ProvidedConverter(env, IComputerAccess.class, Writer.ARG_COMPUTER));
		converter.addFromConverter(new ProvidedConverter(env, ILuaContext.class, Writer.ARG_LUA_CONTEXT));

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
