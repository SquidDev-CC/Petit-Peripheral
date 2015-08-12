package org.squiddev.petit.core;

import org.squiddev.petit.api.ElementHelper;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.tree.IMethodSignature;
import org.squiddev.petit.base.tree.MethodSignature;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;

public final class BaseElementHelper implements ElementHelper {
	private final Environment environment;

	public BaseElementHelper(Environment environment) {
		this.environment = environment;
	}

	@Override
	public AnnotationMirror getAnnotation(Element element, Class<? extends Annotation> annotation) {
		TypeMirror mirror = environment.getTypeHelpers().getMirror(annotation);
		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			DeclaredType annotationType = annotationMirror.getAnnotationType();
			TypeElement annotationElement = (TypeElement) annotationType.asElement();

			if (environment.getTypeUtils().isSameType(annotationElement.asType(), mirror)) {
				return annotationMirror;
			}
		}

		return null;
	}

	@Override
	public Object getValue(AnnotationMirror annotation, String name) {
		for (Map.Entry<? extends ExecutableElement, AnnotationValue> entry : Collections.unmodifiableMap(annotation.getElementValues()).entrySet()) {
			if (entry.getKey().getSimpleName().contentEquals(name)) {
				return entry.getValue().getValue();
			}
		}

		return null;
	}

	@Override
	public Object getValue(Element element, Class<? extends Annotation> annotation, String name) {
		AnnotationMirror mirror = getAnnotation(element, annotation);
		return mirror == null ? null : getValue(mirror, name);
	}

	@Override
	public ExecutableElement getMethod(TypeElement element, ExecutableElement signature) {
		ExecutableElement executable = getMethod(element, new MethodSignature(signature, environment));
		return executable != null && environment.getTypeUtils().isSameType(signature.getReturnType(), executable.getReturnType()) ? executable : null;
	}

	@Override
	public ExecutableElement getMethod(TypeElement element, IMethodSignature signature) {
		String name = signature.getName();
		List<TypeMirror> params = signature.getParameters();
		int size = params.size();
		Types helpers = environment.getTypeUtils();

		for (Element target : environment.getElementUtils().getAllMembers(element)) {
			if (target.getKind() != ElementKind.METHOD || !target.getSimpleName().contentEquals(name)) continue;

			ExecutableElement targetMethod = (ExecutableElement) target;

			List<? extends VariableElement> targetParams = targetMethod.getParameters();
			if (targetParams.size() != size) continue;

			boolean success = true;
			for (int i = 0; i < size; i++) {
				if (!helpers.isSameType(params.get(i), targetParams.get(i).asType())) {
					success = false;
					break;
				}
			}

			if (success) return targetMethod;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<TypeMirror> getTypeMirrors(AnnotationMirror annotation, String name) {
		Object contents = getValue(annotation, name);
		if (contents == null) return null;
		Collection<AnnotationValue> values = (Collection<AnnotationValue>) contents;
		List<TypeMirror> mirrors = new ArrayList<TypeMirror>(values.size());
		for (AnnotationValue value : values) {
			mirrors.add((TypeMirror) value.getValue());
		}
		return mirrors;
	}

	@Override
	public Collection<TypeMirror> getTypeMirrors(Element element, Class<? extends Annotation> annotation, String name) {
		AnnotationMirror mirror = getAnnotation(element, annotation);
		return mirror == null ? null : getTypeMirrors(mirror, name);
	}
}
