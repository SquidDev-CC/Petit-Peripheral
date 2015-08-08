package org.squiddev.petit.core.backend.converter.outbound;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.OutboundConverter;
import org.squiddev.petit.base.backend.AbstractOutboundConverter;
import org.squiddev.petit.core.backend.Utils;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

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

	public static Iterable<OutboundConverter> addBasics(Environment environment) {
		List<OutboundConverter> converters = new ArrayList<OutboundConverter>();

		converters.add(new SimpleConverter(environment, byte.class));
		converters.add(new SimpleConverter(environment, short.class));
		converters.add(new SimpleConverter(environment, int.class));
		converters.add(new SimpleConverter(environment, long.class));
		converters.add(new SimpleConverter(environment, float.class));
		converters.add(new SimpleConverter(environment, double.class));
		converters.add(new SimpleConverter(environment, String.class));
		converters.add(new SimpleConverter(environment, Object.class));

		return converters;
	}
}
