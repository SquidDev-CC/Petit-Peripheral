package org.squiddev.petit.base.tree;

import org.squiddev.petit.api.tree.MethodSignature;
import org.squiddev.petit.api.tree.SyntheticMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSyntheticMethod implements SyntheticMethod {
	private final Collection<TypeMirror> backends;
	private final TypeMirror returnType;
	private final List<TypeMirror> parameters;
	private final String name;
	private final Element element;

	public AbstractSyntheticMethod(Collection<TypeMirror> backends, String name, List<TypeMirror> parameters, TypeMirror returnType, Element element) {
		this.backends = Collections.unmodifiableCollection(backends);
		this.returnType = returnType;
		this.parameters = Collections.unmodifiableList(parameters);
		this.name = name;
		this.element = element;
	}

	public AbstractSyntheticMethod(Collection<TypeMirror> backends, MethodSignature signature, TypeMirror returnType, Element element) {
		this(backends, signature.getName(), signature.getParameters(), returnType, element);
	}

	@Override
	public Collection<TypeMirror> getBackends() {
		return backends;
	}

	@Override
	public TypeMirror getReturnType() {
		return returnType;
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
	public Element getElement() {
		return element;
	}
}
