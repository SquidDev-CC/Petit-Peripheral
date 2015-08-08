package org.squiddev.petit.core.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Segment;
import org.squiddev.petit.api.tree.baked.ArgumentBaked;
import org.squiddev.petit.base.backend.AbstractInboundConverter;
import org.squiddev.petit.core.backend.Utils;

import javax.lang.model.type.TypeMirror;
import java.util.Collections;

/**
 * Simply checks if it is an instance of the class and casts it to it.
 */
public class InstanceOfConverter extends AbstractInboundConverter {
	protected final TypeMirror type;

	public InstanceOfConverter(Environment env, TypeMirror type, String name) {
		super(env, name);
		this.type = type;
	}

	public InstanceOfConverter(Environment env, Class<?> type, String name) {
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
