package org.squiddev.petit.base.transformer;

import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.transformer.Transformer;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAnnotationMirrorTransformer implements Transformer {
	protected final Environment environment;
	private final Class<? extends Annotation> klass;

	public AbstractAnnotationMirrorTransformer(Class<? extends Annotation> klass, Environment environment) {
		this.environment = environment;
		this.klass = klass;
	}

	@Override
	public void transform(IArgumentBuilder builder) {
		if (builder.getElement() == null) return;

		AnnotationMirror annotation = environment.getElementHelpers().getAnnotation(builder.getElement(), klass);
		if (annotation != null) transform(builder, annotation);
	}

	@Override
	public void transform(IClassBuilder builder) {
		if (builder.getElement() == null) return;

		AnnotationMirror annotation = environment.getElementHelpers().getAnnotation(builder.getElement(), klass);
		if (annotation != null) transform(builder, annotation);
	}

	@Override
	public void transform(IMethodBuilder builder) {
		if (builder.getElement() == null) return;

		AnnotationMirror annotation = environment.getElementHelpers().getAnnotation(builder.getElement(), klass);
		if (annotation != null) transform(builder, annotation);
	}

	@Override
	public boolean verify(RoundEnvironment environment) {
		boolean success = true;
		for (Element element : environment.getElementsAnnotatedWith(klass)) {
			AnnotationMirror annotation = this.environment.getElementHelpers().getAnnotation(element, klass);
			if (annotation != null) success &= validate(element, annotation);
		}
		return success;
	}

	public void transform(IClassBuilder klass, AnnotationMirror annotation) {
	}

	public void transform(IMethodBuilder method, AnnotationMirror annotation) {
	}

	public void transform(IArgumentBuilder argument, AnnotationMirror annotation) {
	}

	public boolean validate(Element target, AnnotationMirror annotation) {
		boolean success = true;

		Target t = klass.getClass().getAnnotation(Target.class);
		List<ElementType> type;
		if (t == null) {
			type = Arrays.asList(ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD);
		} else {
			type = Arrays.asList(t.value());
		}

		if (!isValidType(target.getKind(), type)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.ERROR,
				"Unexpected @" + klass.getSimpleName() + " on " + target.getKind() + ", expected " + type,
				target
			);
			success = false;
		}

		if (!hasPeripheral(target)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.WARNING,
				"Cannot find @Peripheral",
				target
			);
			success = false;
		}


		return success;
	}

	protected boolean isValidType(ElementKind kind, Collection<ElementType> type) {
		switch (kind) {
			case METHOD:
				return type.contains(ElementType.METHOD);
			case CLASS:
				return type.contains(ElementType.TYPE);
			case PARAMETER:
				return type.contains(ElementType.PARAMETER);
			default:
				return false;
		}
	}

	protected boolean hasPeripheral(Element element) {
		switch (element.getKind()) {
			case CLASS:
				return element.getAnnotation(Peripheral.class) != null;
			case METHOD:
			case PARAMETER:
				return hasPeripheral(element.getEnclosingElement());
			default:
				return false;
		}
	}

	@Override
	public Collection<Class<? extends Annotation>> getAnnotations() {
		return Collections.<Class<? extends Annotation>>singleton(klass);
	}
}
