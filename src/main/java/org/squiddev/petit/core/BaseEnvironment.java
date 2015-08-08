package org.squiddev.petit.core;

import org.squiddev.petit.api.ElementHelper;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.TypeHelper;

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
public final class BaseEnvironment implements Environment {
	private final TypeHelper typeHelper;
	private final ProcessingEnvironment processingEnvironment;
	private final ElementHelper elementHelper;

	public BaseEnvironment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		this.typeHelper = new BaseTypeHelper(processingEnvironment);
		this.elementHelper = new BaseElementHelper(this);
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
	public ElementHelper getElementHelpers() {
		return elementHelper;
	}

	@Override
	public TypeHelper getTypeHelpers() {
		return typeHelper;
	}
}
