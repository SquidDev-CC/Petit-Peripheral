package org.squiddev.petit.backend.tree;

import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.backend.tree.MethodBaked;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;
import org.squiddev.petit.api.compile.tree.ArgumentKind;
import org.squiddev.petit.api.compile.tree.MethodSignature;
import org.squiddev.petit.api.compile.tree.SyntheticMethod;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class BasicClassBaked implements ClassBaked {
	private final String generatedName;
	private final String name;
	private final Collection<MethodBaked> methods;
	private final Map<MethodSignature, Collection<SyntheticMethod>> synthetics;
	private final TypeElement element;
	private final Environment environment;

	public BasicClassBaked(String generatedName, ClassBuilder builder, Backend backend, Environment environment) {
		this.generatedName = generatedName;
		this.name = builder.getName();
		this.element = builder.getElement();
		this.environment = environment;

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

		/*
			The synthetics method is intentionally modifiable
			as wee need to be able to add to it in parent classes
		 */
		this.synthetics = new HashMap<MethodSignature, Collection<SyntheticMethod>>();

		for (SyntheticMethod synthetic : builder.syntheticMethods()) {
			for (TypeMirror target : synthetic.getBackends()) {
				if (backend.compatibleWith(target)) {
					addSynthetic(synthetic);

					break;
				}
			}
		}
	}

	protected void addSynthetic(SyntheticMethod synthetic) {
		MethodSignature signature = new BasicMethodSignature(synthetic.getName(), synthetic.getParameters(), environment);

		Collection<SyntheticMethod> similar = synthetics.get(signature);
		if (similar == null) synthetics.put(signature, similar = new ArrayList<SyntheticMethod>());

		similar.add(synthetic);
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
	public Map<MethodSignature, Collection<SyntheticMethod>> getSyntheticMethods() {
		return Collections.unmodifiableMap(synthetics);
	}

	@Override
	public TypeElement getElement() {
		return element;
	}
}
