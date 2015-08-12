package org.squiddev.petit.api.tree;

/**
 * The type of a peripheral's parent
 */
public enum ParentKind {
	/**
	 * This type must be implemented on the peripheral
	 */
	REQUIRED,

	/**
	 * This type may be implemented on the peripheral if
	 * the backend supports it
	 */
	OPTIONAL,
}
