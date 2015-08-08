package org.squiddev.petit.base.transformer;

import org.squiddev.petit.api.transformer.ITransformerContainer;
import org.squiddev.petit.api.transformer.Transformer;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TransformerContainer implements ITransformerContainer {
	protected final Collection<Transformer> transformers = new HashSet<Transformer>();

	@Override
	public void add(Transformer transformer) {
		this.transformers.add(transformer);
	}

	@Override
	public void transform(IClassBuilder klass) {
		for (Transformer transformer : this.transformers) {
			transformer.transform(klass);
		}
	}

	@Override
	public void transform(IMethodBuilder method) {
		for (Transformer transformer : this.transformers) {
			transformer.transform(method);
		}
	}

	@Override
	public void transform(IArgumentBuilder arg) {
		for (Transformer transformer : this.transformers) {
			transformer.transform(arg);
		}
	}

	@Override
	public boolean validate(RoundEnvironment environment) {
		boolean success = true;
		for (Transformer transformer : this.transformers) {
			success &= transformer.validate(environment);
		}

		return success;
	}

	@Override
	public Collection<Class<? extends Annotation>> getAnnotations() {
		Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();
		for (Transformer transformer : transformers) {
			annotations.addAll(transformer.getAnnotations());
		}
		return annotations;
	}
}
