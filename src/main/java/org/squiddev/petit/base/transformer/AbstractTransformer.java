package org.squiddev.petit.base.transformer;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.transformer.Transformer;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.annotation.processing.RoundEnvironment;

public abstract class AbstractTransformer implements Transformer {
	protected final Environment environment;

	protected AbstractTransformer(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void transform(IArgumentBuilder argument) {
	}

	@Override
	public void transform(IClassBuilder klass) {
	}

	@Override
	public void transform(IMethodBuilder method) {
	}

	@Override
	public boolean validate(RoundEnvironment environment) {
		return true;
	}
}
