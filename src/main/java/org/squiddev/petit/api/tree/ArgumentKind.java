package org.squiddev.petit.api.tree;


public enum ArgumentKind {
	/**
	 * Required arguments must be passed every time
	 */
	REQUIRED,

	/**
	 * Optional arguments do not have to be passed.
	 *
	 * These must come after all required arguments but if they are then they must still match the validator.
	 */
	OPTIONAL,

	/**
	 * Variable arguments match between 0-n arguments.
	 *
	 * There can be 1 variable argument per method and it must be the last - after any required or optional argument.
	 */
	VARIABLE,

	/**
	 * Provided arguments are not passed by the caller, but instead are gathered from the environment.
	 */
	PROVIDED,
}
