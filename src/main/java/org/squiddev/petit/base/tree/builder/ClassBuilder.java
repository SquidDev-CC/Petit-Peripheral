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
import java.util.Collections;

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
					MethodBuilder builder = new MethodBuilder(this, method);
					methods.add(builder);

					LuaFunction function = method.getAnnotation(LuaFunction.class);

					// Get default isVarArgs
					builder.setVarReturn(function.isVarReturn());

					// Get default error message
					String errorMessage = function.error();
					if (errorMessage != null && !errorMessage.isEmpty()) {
						builder.setErrorMessage(errorMessage);
					}

					// Create the names of this function
					String[] luaName = function.value();
					if (luaName == null || luaName.length == 0 || (luaName.length == 1 && luaName[0].isEmpty())) {
						builder.names().add(method.getSimpleName().toString());
					} else {
						Collections.addAll(builder.names(), luaName);
					}
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
