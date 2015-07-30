package org.squiddev.petit.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.ArgumentType;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.backend.Utils;

import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
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
	public Segment validate(ArgumentBaked argument, String from) {
		if (argument.getArgumentType() != ArgumentType.PROVIDED) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Expected provided type, got " + argument.getArgumentType(), argument.getElement());
		}

		return super.validate(argument, from);
	}

	@Override
	public CodeBlock convert(ArgumentBaked argument, String from) {
		return Utils.block(name);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return Collections.singleton(type);
	}
}
