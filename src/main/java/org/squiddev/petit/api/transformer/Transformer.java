package org.squiddev.petit.api.transformer;

import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Adds or modifies information on nodes
 *
 * @param <A> The annotation that this handles
 */
public interface Transformer<A extends Annotation> {
	/**
	 * Modify a class
	 *
	 * @param klass      The class to modify
	 * @param annotation The annotation to modify
	 */
	void transform(ClassBuilder klass, A annotation);

	/**
	 * Modify a method
	 *
	 * @param method     The method to modify
	 * @param annotation The annotation to modify
	 */
	void transform(MethodBuilder method, A annotation);

	/**
	 * Modify a argument
	 *
	 * @param argument   The argument to modify
	 * @param annotation The annotation to modify
	 */
	void transform(ArgumentBuilder argument, A annotation);

	/**
	 * Validate an annotation on an item.
	 *
	 * @param target     The value to modify
	 * @param annotation The annotation about this item
	 * @return If validation was successful (no errors occured).
	 */
	boolean validate(Element target, A annotation);
}
