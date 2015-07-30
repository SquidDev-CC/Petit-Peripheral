package org.squiddev.petit.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.backend.Utils;

import javax.lang.model.type.TypeMirror;
import java.util.Collections;

/**
 * Simply checks if it is an instance of the class and casts it to it.
 */
public class InstanceofConverter extends AbstractInboundConverter {
	protected final TypeMirror type;

	public InstanceofConverter(Environment env, TypeMirror type, String name) {
		super(env, name);
		this.type = type;
	}

	public InstanceofConverter(Environment env, Class<?> type, String name) {
		this(env, env.getTypeHelpers().getMirror(type), name);
	}

	@Override
	public Segment validate(ArgumentBaked argument, String from) {
		return new Segment("$N instanceof $T", from, type);
	}

	@Override
	public CodeBlock convert(ArgumentBaked argument, String from) {
		return Utils.block("($T)$N", type, from);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return Collections.singleton(type);
	}
}