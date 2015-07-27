package org.squiddev.petit.conversion.to;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.processor.Utils;

import javax.lang.model.type.TypeMirror;

/**
 * Simply writes the result into a Object[]
 */
public class SimpleConverter extends AbstractToLuaConverter {
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
