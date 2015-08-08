package org.squiddev.petit.core.backend.converter.inbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.backend.InboundConverter;
import org.squiddev.petit.api.backend.Segment;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

/**
 * A converter that delegates to the other converter
 * and provides per-argument error messages
 */
public class ArgumentErrorConverter implements InboundConverter {
	protected static final String TEMP_NAME = "temp_arg";
	protected final InboundConverter converter;

	public ArgumentErrorConverter(InboundConverter converter) {
		this.converter = converter;
	}

	@Override
	public boolean matches(ArgumentKind kind, TypeMirror type) {
		return converter.matches(kind, type);
	}

	@Override
	public String getName() {
		return converter.getName();
	}

	@Override
	public CodeBlock preamble(IArgumentBaked argument) {
		return converter.preamble(argument);
	}

	@Override
	public Segment validate(IArgumentBaked argument, String from) {
		Segment segment = converter.validate(argument, from);
		if (segment.isStatement()) return segment;

		return new Segment(
			CodeBlock.builder()
				.add("if(!(")
				.add(segment.getCodeBlock())
				.beginControlFlow("))")
				.addStatement("Object $N = $N", TEMP_NAME, from)
				.add("$[throw new LuaException($S + (", String.format("Expected %s for argument #%s, got ", getName(), argument.getIndex() + 1))
				.add("$N instanceof String ? $S : ", TEMP_NAME, "string")
				.add("$N instanceof Number ? $S : ", TEMP_NAME, "number")
				.add("$N instanceof Boolean ? $S : ", TEMP_NAME, "boolean")
				.add("$N instanceof $T ? $S : $S", TEMP_NAME, Map.class, "table", "nil")
				.add("));\n$]")
				.endControlFlow()
				.build(),
			true
		);
	}

	@Override
	public CodeBlock convert(IArgumentBaked argument, String from) {
		return converter.convert(argument, from);
	}

	protected CodeBlock writeChecker(String name) {
		return CodeBlock.builder()

			.build();
	}
}
