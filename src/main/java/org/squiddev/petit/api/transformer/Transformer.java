package org.squiddev.petit.api.transformer;

import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.annotation.processing.RoundEnvironment;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A generic transformer is not bound to a specific annotation
 */
public interface Transformer {
	/**
	 * Modify a class
	 *
	 * @param klass The class to modify
	 */
	void transform(IClassBuilder klass);

	/**
	 * Modify a method
	 *
	 * @param method The method to modify
	 */
	void transform(IMethodBuilder method);

	/**
	 * Modify a argument
	 *
	 * @param argument The argument to modify
	 */
	void transform(IArgumentBuilder argument);

	/**
	 * Validate an environment
	 *
	 * @param environment The environment to scan in
	 * @return If validation was successful (no errors occurred).
	 */
	boolean validate(RoundEnvironment environment);

	Collection<Class<? extends Annotation>> getAnnotations();
}
