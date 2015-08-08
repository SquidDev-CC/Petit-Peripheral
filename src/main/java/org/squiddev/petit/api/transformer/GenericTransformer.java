package org.squiddev.petit.api.transformer;

import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.annotation.processing.RoundEnvironment;

/**
 * A generic transformer is not bound to a specific annotation
 */
public interface GenericTransformer {
	/**
	 * Modify a class
	 *
	 * @param klass The class to modify
	 */
	void transform(ClassBuilder klass);

	/**
	 * Modify a method
	 *
	 * @param method The method to modify
	 */
	void transform(MethodBuilder method);

	/**
	 * Modify a argument
	 *
	 * @param argument The argument to modify
	 */
	void transform(ArgumentBuilder argument);

	/**
	 * Validate an environment
	 *
	 * @param environment The environment to scan in
	 * @return If validation was successful (no errors occured).
	 */
	boolean validate(RoundEnvironment environment);
}
