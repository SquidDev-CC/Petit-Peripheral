package org.squiddev.petit.base.backend;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.InboundConverter;
import org.squiddev.petit.api.backend.Segment;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;

import javax.lang.model.type.TypeMirror;

public abstract class AbstractInboundConverter implements InboundConverter {
	protected final Environment environment;
	protected final String name;

	public AbstractInboundConverter(Environment env, String name) {
		this.environment = env;
		this.name = name;
	}

	public abstract Iterable<TypeMirror> getTypes();

	@Override
	public boolean matches(ArgumentKind kind, TypeMirror type) {
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
	public CodeBlock convert(IArgumentBaked argument, String from) {
		return null;
	}

	@Override
	public CodeBlock preamble(IArgumentBaked argument) {
		return null;
	}

	@Override
	public Segment validate(IArgumentBaked argument, String from) {
		return null;
	}
}
