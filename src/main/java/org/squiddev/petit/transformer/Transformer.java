package org.squiddev.petit.transformer;

import java.lang.annotation.Annotation;

/**
 * Adds or modifies information on an item
 *
 * @param <T> The type that is modified
 * @param <A> The annotation that this handles
 */
public interface Transformer<T, A extends Annotation> {
	/**
	 * Modify an item
	 *
	 * @param target     The value to modify
	 * @param annotation The annotation about this item
	 */
	void transform(T target, A annotation);
}
