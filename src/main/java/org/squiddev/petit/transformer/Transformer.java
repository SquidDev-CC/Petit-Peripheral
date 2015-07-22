package org.squiddev.petit.transformer;

import org.squiddev.petit.processor.tree.LuaArgument;
import org.squiddev.petit.processor.tree.LuaClass;
import org.squiddev.petit.processor.tree.LuaMethod;

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
	void transform(LuaClass klass, A annotation);

	/**
	 * Modify a method
	 *
	 * @param method     The method to modify
	 * @param annotation The annotation to modify
	 */
	void transform(LuaMethod method, A annotation);

	/**
	 * Modify a argument
	 *
	 * @param argument   The argument to modify
	 * @param annotation The annotation to modify
	 */
	void transform(LuaArgument argument, A annotation);

	/**
	 * Validate an annotation on an item.
	 *
	 * @param target     The value to modify
	 * @param annotation The annotation about this item
	 * @return If validation was successful (no errors occured).
	 */
	boolean validate(Element target, A annotation);
}
