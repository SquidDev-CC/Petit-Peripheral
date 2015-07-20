package org.squiddev.petit.processor;

import org.squiddev.petit.conversion.Converters;
import org.squiddev.petit.conversion.DefaultConverters;
import org.squiddev.petit.transformer.DefaultTransformers;
import org.squiddev.petit.transformer.Transformers;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * The environment used for writing classes
 */
public class Environment {
	public Transformers transformer = DefaultTransformers.add(new Transformers());

	public Converters converters = new Converters();

	public final RoundEnvironment roundEnvironment;

	public final ProcessingEnvironment processingEnvironment;

	public Environment(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment) {
		this.roundEnvironment = roundEnvironment;
		this.processingEnvironment = processingEnvironment;

		DefaultConverters.add(this);
	}
}
