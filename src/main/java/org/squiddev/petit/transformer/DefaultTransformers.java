package org.squiddev.petit.transformer;

import org.squiddev.petit.api.Alias;
import org.squiddev.petit.api.Optional;
import org.squiddev.petit.api.Provided;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.transformer.TransformerContainer;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;
import org.squiddev.petit.api.compile.tree.ArgumentKind;

import java.util.Collections;


/**
 * A series of useful transformers
 */
public final class DefaultTransformers {
	private DefaultTransformers() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static <T extends Environment> T add(T environment) {
		TransformerContainer transformer = environment.getTransformer();

		transformer.add(Alias.class, new AbstractTransformer<Alias>(environment) {
			@Override
			public void transform(MethodBuilder target, Alias annotation) {
				String[] names = annotation.value();
				if (names == null) return;
				Collections.addAll(target.names(), names);
			}
		});

		transformer.add(Optional.class, new AbstractTransformer<Optional>(environment) {
			@Override
			public void transform(ArgumentBuilder argument, Optional annotation) {
				argument.setKind(ArgumentKind.OPTIONAL);
			}
		});

		transformer.add(Provided.class, new AbstractTransformer<Provided>(environment) {
			@Override
			public void transform(ArgumentBuilder argument, Provided annotation) {
				argument.setKind(ArgumentKind.PROVIDED);
			}
		});

		return environment;
	}
}
