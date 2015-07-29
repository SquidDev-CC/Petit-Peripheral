package org.squiddev.petit.transformer;

import org.squiddev.petit.api.Alias;
import org.squiddev.petit.api.Optional;
import org.squiddev.petit.api.Provided;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.transformer.TransformerContainer;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.ArgumentType;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;
import org.squiddev.petit.conversion.from.ProvidedConverter;

import javax.tools.Diagnostic;
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
			public void transform(PeripheralMethod target, Alias annotation) {
				String[] names = annotation.value();
				if (names == null) return;
				Collections.addAll(target.names(), names);
			}
		});

		transformer.add(Optional.class, new AbstractTransformer<Optional>(environment) {
			@Override
			public void transform(Argument argument, Optional annotation) {
				argument.setArgumentType(ArgumentType.OPTIONAL);
			}
		});

		transformer.add(Provided.class, new AbstractTransformer<Provided>(environment) {
			@Override
			public void transform(Argument argument, Provided annotation) {
				argument.setArgumentType(ArgumentType.PROVIDED);
				if (!(argument.getConverter() instanceof ProvidedConverter)) {
					argument.getEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR, "Not a provided converter", argument.getElement());
				}
			}
		});

		return environment;
	}
}
