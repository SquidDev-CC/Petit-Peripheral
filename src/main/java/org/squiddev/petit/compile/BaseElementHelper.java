package org.squiddev.petit.compile;

import org.squiddev.petit.api.compile.ElementHelper;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.tree.MethodSignature;
import org.squiddev.petit.backend.tree.BasicMethodSignature;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
		ExecutableElement executable = getMethod(element, new BasicMethodSignature(signature, environment));
		return executable != null && environment.getTypeUtils().isSameType(signature.getReturnType(), executable.getReturnType()) ? executable : null;
	}

	@Override
	public ExecutableElement getMethod(TypeElement element, MethodSignature signature) {
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
}