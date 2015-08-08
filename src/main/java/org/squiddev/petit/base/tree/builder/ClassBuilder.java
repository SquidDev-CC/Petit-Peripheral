package org.squiddev.petit.base.tree.builder;

import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Data about the class we are generating
 */
public class ClassBuilder implements IClassBuilder {
	private final TypeElement klass;
	private final String name;
	private final Collection<IMethodBuilder> methods;
	private final Collection<ISyntheticMethod> synthetics = new ArrayList<ISyntheticMethod>();

	public ClassBuilder(String name, TypeElement klass) {
		this.name = name;
		this.klass = klass;

		// Gather methods
		Collection<IMethodBuilder> methods = this.methods = new ArrayList<IMethodBuilder>();
		for (Element element : klass.getEnclosedElements()) {
			if (element.getKind() == ElementKind.METHOD) {
				ExecutableElement method = (ExecutableElement) element;
				if (method.getAnnotation(LuaFunction.class) != null) {
					methods.add(new MethodBuilder(this, method));
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
	public Collection<IMethodBuilder> methods() {
		return methods;
	}

	@Override
	public Collection<ISyntheticMethod> syntheticMethods() {
		return synthetics;
	}
}
