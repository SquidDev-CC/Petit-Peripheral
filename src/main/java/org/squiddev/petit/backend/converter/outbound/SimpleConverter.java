package org.squiddev.petit.backend.converter.outbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.backend.Utils;

import javax.lang.model.type.TypeMirror;

/**
 * Simply writes the result into a Object[]
 */
public class SimpleConverter extends AbstractOutboundConverter {
	public SimpleConverter(Environment env, Class<?> type) {
		super(env, type);
	}

	public SimpleConverter(Environment env, TypeMirror type) {
		super(env, type);
	}

	@Override
	public CodeBlock convertTo(String fromToken) {
		return Utils.block("new Object[] { $N }", fromToken);
	}
}
