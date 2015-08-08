package org.squiddev.petit.api.transformer;

/**
 * A collection of transformers for various objects.
 *
 * Finds annotations on the element and applies the correct transformer.
 */
public interface ITransformerContainer extends Transformer {
	/**
	 * Add a transformer
	 *
	 * @param transformer The transformer to add
	 */
	void add(Transformer transformer);
}
