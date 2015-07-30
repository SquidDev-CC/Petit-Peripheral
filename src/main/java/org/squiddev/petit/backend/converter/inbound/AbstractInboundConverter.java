package org.squiddev.petit.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;

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
	public CodeBlock convert(ArgumentBaked argument, String from) {
		return null;
	}

	@Override
	public CodeBlock preamble(ArgumentBaked argument) {
		return null;
	}

	@Override
	public Segment validate(ArgumentBaked argument, String from) {
		return null;
	}
}
