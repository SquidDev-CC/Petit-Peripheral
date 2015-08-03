package org.squiddev.petit.api.compile.tree;

import javax.lang.model.element.Element;

public interface Node<T extends Element> {
	/**
	 * Get the element for this node.
	 *
	 * @return The element for this node. Note, this may be {@code null} for synthetic objects.
	 */
	T getElement();
}
