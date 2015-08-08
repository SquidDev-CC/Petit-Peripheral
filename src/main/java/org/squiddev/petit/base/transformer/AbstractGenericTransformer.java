package org.squiddev.petit.base.transformer;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.transformer.GenericTransformer;
import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.annotation.processing.RoundEnvironment;

public abstract class AbstractGenericTransformer implements GenericTransformer {
	protected final Environment environment;

	protected AbstractGenericTransformer(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void transform(ArgumentBuilder argument) {
	}

	@Override
	public void transform(ClassBuilder klass) {
	}

	@Override
	public void transform(MethodBuilder method) {
	}

	@Override
	public boolean validate(RoundEnvironment environment) {
		return true;
	}
}
