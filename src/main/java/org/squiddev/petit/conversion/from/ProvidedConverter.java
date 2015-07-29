package org.squiddev.petit.conversion.from;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.Segment;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.processor.Utils;

import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;

/**
 * A converter for provided types.
 */
public class ProvidedConverter extends AbstractFromLuaConverter {
	protected final TypeMirror type;

	public ProvidedConverter(Environment env, TypeMirror type, String name) {
		super(env, name);
		this.type = type;
	}

	public ProvidedConverter(Environment env, Class<?> type, String name) {
		this(env, env.getTypeHelpers().getMirror(type), name);
	}

	@Override
	public Segment validate(Argument argument, String from) {
		if (argument.getArgumentType() != ArgumentType.PROVIDED) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Expected provided type, got " + argument.getArgumentType(), argument.getElement());
		}

		return super.validate(argument, from);
	}

	@Override
	public CodeBlock convert(Argument argument, String from) {
		return Utils.block(name);
	}

	@Override
	public Iterable<TypeMirror> getTypes() {
		return Collections.singleton(type);
	}
}
