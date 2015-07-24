package org.squiddev.petit.conversion.from;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractFromLuaConverter implements FromLuaConverter {
	protected final Environment environment;
	protected final String name;

	public AbstractFromLuaConverter(Environment env, String name) {
		this.environment = env;
		this.name = name;
	}

	public abstract Iterable<TypeMirror> getTypes();

	@Override
	public boolean matches(TypeMirror type) {
		for (TypeMirror match : getTypes()) {
			if (environment.getTypeUtils().isSameType(match, type)) return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean requiresVariable() {
		return false;
	}
}
