package org.squiddev.petit.api.compile.tree;

import org.squiddev.petit.processor.Environment;

import javax.lang.model.element.Element;

public interface Node<T extends Element> {
	/**
	 * Process the node checking all parameters are correct.
	 *
	 * @return If it was successful setting up.
	 */
	boolean process();

	/**
	 * Get the environment for this node
	 *
	 * @return The node's environment.
	 */
	Environment getEnvironment();

	/**
	 * Get the element for this node
	 *
	 * @return The element for this node.
	 */
	T getElement();
}
