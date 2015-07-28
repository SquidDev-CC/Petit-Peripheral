package org.squiddev.petit.processor;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.TypeHelper;
import org.squiddev.petit.api.compile.converter.ConverterContainer;
import org.squiddev.petit.api.compile.transformer.TransformerContainer;
import org.squiddev.petit.conversion.Converters;
import org.squiddev.petit.conversion.DefaultConverters;
import org.squiddev.petit.transformer.DefaultTransformers;
import org.squiddev.petit.transformer.Transformers;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Locale;
import java.util.Map;

/**
 * The environment used for writing classes
 */
public class BaseEnvironment implements Environment {
	private final TransformerContainer transformer = new Transformers();
	private final ConverterContainer converters = new Converters();
	private final TypeHelper typeHelper;
	private final ProcessingEnvironment processingEnvironment;

	public BaseEnvironment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		this.typeHelper = new BaseTypeHelper(processingEnvironment);

		DefaultConverters.add(this);
		DefaultTransformers.add(this);
	}

	@Override
	public Map<String, String> getOptions() {
		return processingEnvironment.getOptions();
	}

	@Override
	public Messager getMessager() {
		return processingEnvironment.getMessager();
	}

	@Override
	public Filer getFiler() {
		return processingEnvironment.getFiler();
	}

	@Override
	public Elements getElementUtils() {
		return processingEnvironment.getElementUtils();
	}

	@Override
	public Types getTypeUtils() {
		return processingEnvironment.getTypeUtils();
	}

	@Override
	public SourceVersion getSourceVersion() {
		return processingEnvironment.getSourceVersion();
	}

	@Override
	public Locale getLocale() {
		return processingEnvironment.getLocale();
	}

	@Override
	public TypeHelper getTypeHelpers() {
		return typeHelper;
	}

	@Override
	public TransformerContainer getTransformer() {
		return transformer;
	}

	@Override
	public ConverterContainer getConverter() {
		return converters;
	}
}
