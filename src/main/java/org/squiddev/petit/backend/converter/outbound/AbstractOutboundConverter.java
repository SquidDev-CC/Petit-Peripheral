package org.squiddev.petit.backend.converter.outbound;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.OutboundConverter;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractOutboundConverter implements OutboundConverter {
	private final TypeMirror type;
	private final Environment environment;

	public AbstractOutboundConverter(Environment env, TypeMirror type) {
		this.type = type;
		this.environment = env;
	}

	public AbstractOutboundConverter(Environment env, Class<?> type) {
		this(env, env.getTypeHelpers().getMirror(type));
	}

	@Override
	public boolean matches(TypeMirror type) {
		return environment.getTypeUtils().isSameType(this.type, type);
	}
}
