package org.squiddev.petit.conversion.to;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.ToLuaConverter;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractToLuaConverter implements ToLuaConverter {
	private final TypeMirror type;
	private final Environment environment;

	public AbstractToLuaConverter(Environment env, TypeMirror type) {
		this.type = type;
		this.environment = env;
	}

	public AbstractToLuaConverter(Environment env, Class<?> type) {
		this(env, env.getTypeHelpers().getMirror(type));
	}

	@Override
	public boolean matches(TypeMirror type) {
		return environment.getTypeUtils().isSameType(this.type, type);
	}
}
