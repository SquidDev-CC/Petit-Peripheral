package org.squiddev.petit.transformer.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Data about the class we are generating
 */
public class BasicClassBuilder implements ClassBuilder {
	private final TypeElement klass;
	private final String name;
	private final Set<MethodBuilder> methods;

	public BasicClassBuilder(String name, TypeElement klass) {
		this.name = name;
		this.klass = klass;

		// Gather methods
		Set<MethodBuilder> methods = this.methods = new HashSet<MethodBuilder>();
		for (Element element : klass.getEnclosedElements()) {
			if (element.getKind() == ElementKind.METHOD) {
				ExecutableElement method = (ExecutableElement) element;
				if (method.getAnnotation(LuaFunction.class) != null) {
					methods.add(new BasicMethodBuilder(this, method));
				}
			}
		}
	}

	@Override
	public TypeElement getElement() {
		return klass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<MethodBuilder> methods() {
		return methods;
	}
}
