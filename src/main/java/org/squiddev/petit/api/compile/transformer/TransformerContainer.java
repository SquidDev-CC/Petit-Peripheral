package org.squiddev.petit.api.compile.transformer;

import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A collection of transformers for various objects.
 *
 * Finds annotations on the element and applies the correct transformer.
 */
public interface TransformerContainer {
	<A extends Annotation> void add(Class<A> annotation, Transformer<A> transformer);

	void transform(PeripheralClass klass);

	void transform(PeripheralMethod method);

	void transform(Argument arg);

	boolean validate(RoundEnvironment environment);

	Collection<Class<? extends Annotation>> annotations();
}
