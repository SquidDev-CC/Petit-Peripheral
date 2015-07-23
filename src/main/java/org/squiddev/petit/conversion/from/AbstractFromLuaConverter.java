package org.squiddev.petit.conversion.from;

import org.squiddev.petit.processor.Environment;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractFromLuaConverter implements FromLuaConverter {
	protected final Environment environment;

	public AbstractFromLuaConverter(Environment env) {
		this.environment = env;
	}

	public abstract Iterable<TypeMirror> getTypes();

	@Override
	public boolean matches(TypeMirror type) {
		for (TypeMirror match : getTypes()) {
			if (environment.processingEnvironment.getTypeUtils().isSameType(match, type)) return true;
		}
		return false;
	}

	@Override
	public boolean requiresVariable() {
		return false;
	}
}
