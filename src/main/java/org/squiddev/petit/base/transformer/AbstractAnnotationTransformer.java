package org.squiddev.petit.base.transformer;

import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.transformer.Transformer;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.annotation.processing.RoundEnvironment;
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

public abstract class AbstractAnnotationTransformer<T extends Annotation> implements Transformer {
	protected final Environment environment;
	private final Class<? extends T> klass;

	public AbstractAnnotationTransformer(Class<? extends T> klass, Environment environment) {
		this.environment = environment;
		this.klass = klass;
	}

	@Override
	public void transform(IArgumentBuilder argument) {
		if (argument.getElement() == null) return;

		T annotation = argument.getElement().getAnnotation(klass);
		if (annotation != null) transform(argument, annotation);
	}

	@Override
	public void transform(IClassBuilder klass) {
		if (klass.getElement() == null) return;

		T annotation = klass.getElement().getAnnotation(this.klass);
		if (annotation != null) transform(klass, annotation);
	}

	@Override
	public void transform(IMethodBuilder method) {
		if (method.getElement() == null) return;

		T annotation = method.getElement().getAnnotation(klass);
		if (annotation != null) transform(method, annotation);
	}

	@Override
	public boolean validate(RoundEnvironment environment) {
		boolean success = true;
		for (Element element : environment.getElementsAnnotatedWith(klass)) {
			success &= validate(element, element.getAnnotation(klass));
		}
		return success;
	}

	public void transform(IClassBuilder klass, T annotation) {
	}

	public void transform(IMethodBuilder method, T annotation) {
	}

	public void transform(IArgumentBuilder argument, T annotation) {
	}

	public boolean validate(Element target, T annotation) {
		boolean success = true;

		Class<?> base = annotation.getClass().getInterfaces()[0];
		Target t = base.getClass().getAnnotation(Target.class);
		List<ElementType> type;
		if (t == null) {
			type = Arrays.asList(ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD);
		} else {
			type = Arrays.asList(t.value());
		}

		if (!isValidType(target.getKind(), type)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.ERROR,
				"Unexpected @" + base.getSimpleName() + " on " + target.getKind() + ", expected " + type,
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
