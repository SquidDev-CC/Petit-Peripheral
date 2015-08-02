package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.ArgumentKind;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BasicClassBaked implements ClassBaked {
	private final String generatedName;
	private final String name;
	private final Collection<MethodBaked> methods;
	private final TypeElement element;

	public BasicClassBaked(String generatedName, ClassBuilder builder, Backend backend) {
		this.generatedName = generatedName;
		this.name = builder.getName();
		this.element = builder.getElement();

		Collection<MethodBaked> methods = new ArrayList<MethodBaked>();
		this.methods = Collections.unmodifiableCollection(methods);

		for (MethodBuilder method : builder.methods()) {
			boolean include = true;
			for (ArgumentBuilder argument : method.getArguments()) {
				InboundConverter converter = backend.getInboundConverter(argument.getKind(), argument.getType());
				if (converter == null) {
					if (argument.getKind() == ArgumentKind.PROVIDED) {
						include = false;
						break;
					}
				}
			}

			if (include) methods.add(new BasicMethodBaked(method, this));
		}
	}

	@Override
	public String getGeneratedName() {
		return generatedName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<MethodBaked> getMethods() {
		return methods;
	}

	@Override
	public TypeElement getElement() {
		return element;
	}
}
