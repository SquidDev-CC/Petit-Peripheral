package org.squiddev.petit.conversion.to;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.processor.Segment;

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
	public Segment convertTo(String fromToken) {
		return new Segment("new Object[] { $N }", fromToken);
	}
}
