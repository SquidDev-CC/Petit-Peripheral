package org.squiddev.petit.transformer;

import org.squiddev.petit.processor.tree.LuaArgument;
import org.squiddev.petit.processor.tree.LuaClass;
import org.squiddev.petit.processor.tree.LuaMethod;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of transformers for various objects.
 *
 * Finds annotations on the element and applies the correct transformer.
 */
public class Transformers {
	protected final Map<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> transformers = new HashMap<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>>();

	public <A extends Annotation> void add(Class<A> annotation, Transformer<A> transformer) {
		if (transformers.containsKey(annotation)) {
			throw new IllegalArgumentException("Cannot override " + annotation);
		}
		transformers.put(annotation, new AnnotationWrapper<A>(transformer));
	}

	public void transform(LuaClass klass) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = klass.klass.getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(klass, annotation);
		}
	}

	public void transform(LuaMethod method) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = method.method.getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(method, annotation);
		}
	}

	public void transform(LuaArgument arg) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			Annotation annotation = arg.parameter.getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(arg, annotation);
		}
	}

	public boolean validate(RoundEnvironment environment) {
		boolean success = true;
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<? extends Annotation>> entry : transformers.entrySet()) {
			for (Element element : environment.getElementsAnnotatedWith(entry.getKey())) {
				success &= entry.getValue().validate(element, element.getAnnotation(entry.getKey()));
			}
		}

		return success;
	}

	public Collection<Class<? extends Annotation>> annotations() {
		return transformers.keySet();
	}

	protected static class AnnotationWrapper<A extends Annotation> {
		public final Transformer<A> transformer;

		private AnnotationWrapper(Transformer<A> transformer) {
			this.transformer = transformer;
		}

		@SuppressWarnings("unchecked")
		public void transform(LuaClass item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public void transform(LuaMethod item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public void transform(LuaArgument item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}

		@SuppressWarnings("unchecked")
		public boolean validate(Element item, Annotation annotation) {
			return transformer.validate(item, (A) annotation);
		}
	}
}
