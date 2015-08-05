package org.squiddev.petit.transformer;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.transformer.GenericTransformer;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

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
