package org.squiddev.petit.api.transformer;

import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A collection of transformers for various objects.
 *
 * Finds annotations on the element and applies the correct transformer.
 */
public interface TransformerContainer {
	void add(GenericTransformer transformer);

	<A extends Annotation> void add(Class<A> annotation, Transformer<A> transformer);

	void transform(ClassBuilder klass);

	void transform(MethodBuilder method);

	void transform(ArgumentBuilder arg);

	boolean validate(RoundEnvironment environment);

	Collection<Class<? extends Annotation>> annotations();
}
