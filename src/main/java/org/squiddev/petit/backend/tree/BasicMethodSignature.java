package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.tree.MethodSignature;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

public final class BasicMethodSignature implements MethodSignature {
	private final List<TypeMirror> parameters;
	private final String name;
	private final Types helpers;

	public BasicMethodSignature(String name, List<TypeMirror> parameters, Environment environment) {
		this.name = name;
		this.parameters = parameters;
		this.helpers = environment.getTypeUtils();
	}

	public BasicMethodSignature(String name, Environment environment, Class<?>... parameters) {
		this.name = name;
		this.helpers = environment.getTypeUtils();
		List<TypeMirror> params = this.parameters = new ArrayList<TypeMirror>(parameters.length);
		for (Class<?> param : parameters) {
			params.add(environment.getTypeHelpers().getMirror(param));
		}
	}

	public BasicMethodSignature(ExecutableElement element, Environment environment) {
		this.name = element.getSimpleName().toString();
		this.helpers = environment.getTypeUtils();
		List<TypeMirror> params = this.parameters = new ArrayList<TypeMirror>(element.getParameters().size());
		for (VariableElement param : element.getParameters()) {
			params.add(param.asType());
		}
	}

	@Override
	public List<TypeMirror> getParameters() {
		return parameters;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MethodSignature)) return false;

		MethodSignature that = (MethodSignature) o;

		if (!name.equals(that.getName())) return false;

		List<TypeMirror> a = parameters, b = that.getParameters();
		if (a.size() != b.size()) return false;

		int size = a.size();
		for (int i = 0; i < size; i++) {
			if (!helpers.isSameType(a.get(i), b.get(i))) return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return parameters.hashCode() * 31 + name.hashCode();
	}
}
