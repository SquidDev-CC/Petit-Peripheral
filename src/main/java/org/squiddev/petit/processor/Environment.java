package org.squiddev.petit.processor;

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
public class Environment implements org.squiddev.petit.api.compile.Environment {
	public Transformers transformer = new Transformers();

	public Converters converters = new Converters();

	private final org.squiddev.petit.api.compile.TypeHelpers typeHelpers = new TypeHelpers(this);
	private final ProcessingEnvironment processingEnvironment;

	public Environment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;

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
	public org.squiddev.petit.api.compile.TypeHelpers getTypeHelpers() {
		return typeHelpers;
	}
}
