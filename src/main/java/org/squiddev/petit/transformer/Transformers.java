package org.squiddev.petit.transformer;

import org.squiddev.petit.api.compile.transformer.Transformer;
import org.squiddev.petit.api.compile.transformer.TransformerContainer;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Transformers implements TransformerContainer {
	protected final Map<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> transformers = new HashMap<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>>();

	@Override
	public <A extends Annotation> void add(Class<A> annotation, Transformer<A> transformer) {
		if (transformers.containsKey(annotation)) {
			throw new IllegalArgumentException("Cannot override " + annotation);
		}
		transformers.put(annotation, new AnnotationWrapper<A>(transformer));
	}

	@Override
	public void transform(PeripheralClass klass) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = klass.getElement().getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(klass, annotation);
		}
	}

	@Override
	public void transform(PeripheralMethod method) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = method.getElement().getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(method, annotation);
		}
	}

	@Override
	public void transform(Argument arg) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = arg.getElement().getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(arg, annotation);
		}
	}

	@Override
	public boolean validate(RoundEnvironment environment) {
		boolean success = true;
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			for (Element element : environment.getElementsAnnotatedWith(entry.getKey())) {
				success &= entry.getValue().validate(element, element.getAnnotation(entry.getKey()));
			}
		}

		return success;
	}

	@Override
	public Collection<Class<? extends Annotation>> annotations() {
		return transformers.keySet();
	}

	protected static class AnnotationWrapper<A extends Annotation> {
		public final Transformer<A> transformer;

		private AnnotationWrapper(Transformer<A> transformer) {
			this.transformer = transformer;
		}

		@SuppressWarnings("unchecked")
		public void transform(PeripheralClass item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public void transform(PeripheralMethod item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public void transform(Argument item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public boolean validate(Element item, Annotation annotation) {
			return transformer.validate(item, (A) annotation);
		}
	}
}
