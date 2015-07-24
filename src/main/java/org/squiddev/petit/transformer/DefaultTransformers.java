package org.squiddev.petit.transformer;

import org.squiddev.petit.api.Alias;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;
import org.squiddev.petit.processor.Environment;

import java.util.Collections;


/**
 * A series of useful transformers
 */
public final class DefaultTransformers {
	private DefaultTransformers() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Environment> T add(T environment) {
		Transformers transformer = environment.transformer;

		transformer.add(Alias.class, new AbstractTransformer<Alias>(environment) {
			@Override
			public void transform(PeripheralMethod target, Alias annotation) {
				String[] names = annotation.value();
				if (names == null) return;
				Collections.addAll(target.names(), names);
			}
		});

		return environment;
	}
}
