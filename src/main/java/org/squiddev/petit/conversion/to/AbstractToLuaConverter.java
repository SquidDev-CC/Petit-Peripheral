package org.squiddev.petit.conversion.to;

import org.squiddev.petit.processor.Environment;
import org.squiddev.petit.processor.TypeHelpers;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractToLuaConverter implements ToLuaConverter {
	private final TypeMirror type;
	private final Environment environment;

	public AbstractToLuaConverter(Environment env, TypeMirror type) {
		this.type = type;
		this.environment = env;
	}

	public AbstractToLuaConverter(Environment env, Class<?> type) {
		this(env, TypeHelpers.getMirror(env.processingEnvironment, type));
	}

	@Override
	public boolean matches(TypeMirror type) {
		return environment.processingEnvironment.getTypeUtils().isSameType(this.type, type);
	}

	@Override
	public boolean requiresVariable() {
		return false;
	}
}
