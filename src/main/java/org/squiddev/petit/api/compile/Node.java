package org.squiddev.petit.api.compile;

import javax.lang.model.element.Element;

public interface Node<T extends Element> {
	/**
	 * Get the element for this node
	 *
	 * @return The element for this node.
	 */
	T getElement();
}
