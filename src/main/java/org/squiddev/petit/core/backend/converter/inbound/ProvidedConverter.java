package org.squiddev.petit.core.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Segment;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.base.backend.AbstractInboundConverter;
import org.squiddev.petit.core.backend.Utils;

import javax.lang.model.type.TypeMirror;
import java.util.Collections;

/**
 * A converter for provided types.
 */
public class ProvidedConverter extends AbstractInboundConverter {
	protected final TypeMirror type;

	public ProvidedConverter(Environment env, TypeMirror type, String name) {
		super(env, name);
		this.type = type;
	}

	public ProvidedConverter(Environment env, Class<?> type, String name) {
		this(env, env.getTypeHelpers().getMirror(type), name);
	}

	@Override
	public boolean matches(ArgumentKind kind, TypeMirror type) {
		return kind == ArgumentKind.PROVIDED && super.matches(kind, type);
	}

	@Override
	public Segment validate(IArgumentBaked argument, String from) {
		return super.validate(argument, from);
	}

	@Override
	public CodeBlock convert(IArgumentBaked argument, String from) {
		return Utils.block(name);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return Collections.singleton(type);
	}
}
