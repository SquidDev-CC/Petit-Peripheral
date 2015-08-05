package org.squiddev.petit.api.compile;

import org.squiddev.petit.api.compile.transformer.TransformerContainer;

import javax.annotation.processing.ProcessingEnvironment;

public interface Environment extends ProcessingEnvironment {
	ElementHelper getElementHelpers();

	TypeHelper getTypeHelpers();

	TransformerContainer getTransformer();
}
