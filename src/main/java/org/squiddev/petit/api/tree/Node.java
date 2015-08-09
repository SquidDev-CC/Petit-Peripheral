package org.squiddev.petit.api.tree;

import javax.lang.model.element.Element;

public interface Node {
	/**
	 * Get the element for this node.
	 *
	 * @return The element for this node. Note, this may be {@code null} for synthetic objects.
	 */
	Element getElement();
}
