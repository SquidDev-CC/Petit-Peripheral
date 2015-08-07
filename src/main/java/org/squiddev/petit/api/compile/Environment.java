package org.squiddev.petit.api.compile;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * The environment is an extension to {@link ProcessingEnvironment}
 * adding some additional helper classes
 */
public interface Environment extends ProcessingEnvironment {
	ElementHelper getElementHelpers();

	TypeHelper getTypeHelpers();
}
