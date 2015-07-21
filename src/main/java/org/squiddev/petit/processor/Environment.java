package org.squiddev.petit.processor;

import org.squiddev.petit.conversion.Converters;
import org.squiddev.petit.conversion.DefaultConverters;
import org.squiddev.petit.transformer.DefaultTransformers;
import org.squiddev.petit.transformer.Transformers;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Locale;
import java.util.Map;

/**
 * The environment used for writing classes
 */
public class Environment implements ProcessingEnvironment {
	public Transformers transformer = DefaultTransformers.add(new Transformers());

	public Converters converters = new Converters();

	public final TypeHelpers typeHelpers = new TypeHelpers(this);

	public final RoundEnvironment roundEnvironment;

	public final ProcessingEnvironment processingEnvironment;

	public Environment(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
		this.roundEnvironment = roundEnvironment;
		this.processingEnvironment = processingEnvironment;

		DefaultConverters.add(this);
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
}
