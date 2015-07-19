package org.squiddev.petit.transformer;

import org.squiddev.petit.processor.tree.LuaArgument;
import org.squiddev.petit.processor.tree.LuaClass;
import org.squiddev.petit.processor.tree.LuaMethod;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of transformers for various objects.
 *
 * Finds annotations on the element and applies the correct transformer.
 */
public class Transformers {
	protected final Map<Class<? extends Annotation>, AnnotationWrapper<LuaClass, ? extends Annotation>> classTransformers = new HashMap<Class<? extends Annotation>, AnnotationWrapper<LuaClass, ? extends Annotation>>();
	protected final Map<Class<? extends Annotation>, AnnotationWrapper<LuaMethod, ? extends Annotation>> methodTransformers = new HashMap<Class<? extends Annotation>, AnnotationWrapper<LuaMethod, ? extends Annotation>>();
	protected final Map<Class<? extends Annotation>, AnnotationWrapper<LuaArgument, ? extends Annotation>> argumentTransformers = new HashMap<Class<? extends Annotation>, AnnotationWrapper<LuaArgument, ? extends Annotation>>();

	public <A extends Annotation> void addClassTransformer(Class<A> annotation, Transformer<LuaClass, A> transformer) {
		if (classTransformers.containsKey(annotation)) {
			throw new IllegalArgumentException("Cannot override " + annotation);
		}
		classTransformers.put(annotation, new AnnotationWrapper<LuaClass, A>(transformer));
	}

	public <A extends Annotation> void addMethodTransformer(Class<A> annotation, Transformer<LuaMethod, A> transformer) {
		if (methodTransformers.containsKey(annotation)) {
			throw new IllegalArgumentException("Cannot override " + annotation);
		}
		methodTransformers.put(annotation, new AnnotationWrapper<LuaMethod, A>(transformer));
	}

	public <A extends Annotation> void addArgumentTransformer(Class<A> annotation, Transformer<LuaArgument, A> transformer) {
		if (argumentTransformers.containsKey(annotation)) {
			throw new IllegalArgumentException("Cannot override " + annotation);
		}
		argumentTransformers.put(annotation, new AnnotationWrapper<LuaArgument, A>(transformer));
	}

	public void transform(LuaClass klass) {
		transform(klass.klass, klass, classTransformers);
	}

	public void transform(LuaMethod method) {
		transform(method.method, method, methodTransformers);
	}

	public void transform(LuaArgument arg) {
		transform(arg.parameter, arg, argumentTransformers);
	}

	protected <T> void transform(Element element, T transform, Map<Class<? extends Annotation>, AnnotationWrapper<T, ? extends Annotation>> processors) {
		for (Map.Entry<Class<? extends Annotation>, AnnotationWrapper<T, ? extends Annotation>> entry : processors.entrySet()) {
			Annotation annotation = element.getAnnotation(entry.getKey());
			if (annotation != null) entry.getValue().transform(transform, annotation);
		}
	}

	protected static class AnnotationWrapper<T, A extends Annotation> {
		public final Transformer<T, A> transformer;

		private AnnotationWrapper(Transformer<T, A> transformer) {
			this.transformer = transformer;
		}

		@SuppressWarnings("unchecked")
		public void transform(T item, Annotation annotation) {
			transformer.transform(item, (A) annotation);
		}
	}
}
