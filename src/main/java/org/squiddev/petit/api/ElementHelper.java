package org.squiddev.petit.api;

import org.squiddev.petit.api.tree.IMethodSignature;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Various helpers for elements
 */
public interface ElementHelper {
	/**
	 * Get an annotation from a element
	 *
	 * @param element    The element to extract from
	 * @param annotation The annotation to get
	 * @return The annotation or {@code null} if not found
	 */
	AnnotationMirror getAnnotation(Element element, Class<? extends Annotation> annotation);

	/**
	 * Get a value from an annotation
	 *
	 * @param annotation The annotation to extract from
	 * @param name       The name of the field
	 * @return The extracted value or {@code null} if it cannot be found
	 */
	Object getValue(AnnotationMirror annotation, String name);

	/**
	 * Get a value from an annotation
	 *
	 * @param element    The element to extract from
	 * @param annotation The annotation to extract from
	 * @param name       The name of the field
	 * @return The extracted value or {@code null} if it cannot be found
	 */
	Object getValue(Element element, Class<? extends Annotation> annotation, String name);

	/**
	 * Extension or {@link #getValue(AnnotationMirror, String)} but with {@code Class<?>[]} support.
	 *
	 * @param annotation The annotation to extract from
	 * @param name       The name of the field
	 * @return Collection of type mirrors or @{code null} if none found.
	 */
	Collection<TypeMirror> getTypeMirrors(AnnotationMirror annotation, String name);

	/**
	 * Extension or {@link #getValue(Element, Class, String)} but with {@code Class<?>[]} support.
	 *
	 * @param element    The element to extract from
	 * @param annotation The annotation to extract from
	 * @param name       The name of the field
	 * @return Collection of type mirrors or @{code null} if none found.
	 */
	Collection<TypeMirror> getTypeMirrors(Element element, Class<? extends Annotation> annotation, String name);

	/**
	 * Find an executable element matching this one
	 *
	 * @param element   The element to find on
	 * @param signature The signature to match
	 * @return The found element or {@code null} if not found
	 */
	ExecutableElement getMethod(TypeElement element, ExecutableElement signature);

	/**
	 * Find an executable element matching this one
	 *
	 * @param element   The element to find on
	 * @param signature The signature to match
	 * @return The found element or {@code null} if not found
	 */
	ExecutableElement getMethod(TypeElement element, IMethodSignature signature);
}
