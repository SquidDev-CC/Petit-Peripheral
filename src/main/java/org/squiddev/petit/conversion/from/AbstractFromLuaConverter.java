package org.squiddev.petit.conversion.from;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.Segment;
import org.squiddev.petit.api.compile.converter.FromLuaConverter;
import org.squiddev.petit.api.compile.tree.Argument;

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
	public CodeBlock convert(Argument argument, String from) {
		return null;
	}

	@Override
	public CodeBlock preamble(Argument argument) {
		return null;
	}

	@Override
	public Segment validate(Argument argument, String from) {
		return null;
	}
}
