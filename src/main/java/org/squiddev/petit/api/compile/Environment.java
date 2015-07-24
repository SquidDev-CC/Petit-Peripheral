package org.squiddev.petit.api.compile;

import org.squiddev.petit.api.compile.converter.ConverterContainer;
import org.squiddev.petit.api.compile.transformer.TransformerContainer;

import javax.annotation.processing.ProcessingEnvironment;

public interface Environment extends ProcessingEnvironment {
	TypeHelper getTypeHelpers();

	TransformerContainer getTransformer();

	ConverterContainer getConverter();
}
