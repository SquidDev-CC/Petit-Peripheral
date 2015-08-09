package org.squiddev.petit.base.tree.baked;

import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.backend.InboundConverter;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.IMethodSignature;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;
import org.squiddev.petit.base.tree.MethodSignature;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class ClassBaked implements IClassBaked {
	private final String generatedName;
	private final String name;
	private final Collection<IMethodBaked> methods;
	private final Map<IMethodSignature, Collection<ISyntheticMethod>> synthetics;
	private final Element element;
	private final Environment environment;

	public ClassBaked(String generatedName, IClassBuilder builder, Backend backend, Environment environment) {
		this.generatedName = generatedName;
		this.name = builder.getName();
		this.element = builder.getElement();
		this.environment = environment;

		Collection<IMethodBaked> methods = new ArrayList<IMethodBaked>();
		this.methods = Collections.unmodifiableCollection(methods);

		for (IMethodBuilder method : builder.methods()) {
			boolean include = true;
			for (IArgumentBuilder argument : method.getArguments()) {
				InboundConverter converter = backend.getInboundConverter(argument.getKind(), argument.getType());
				if (converter == null) {
					if (argument.getKind() == ArgumentKind.PROVIDED) {
						include = false;
						break;
					}
				}
			}

			if (include) methods.add(new MethodBaked(method, this));
		}

		/*
			The synthetics method is intentionally modifiable
			as wee need to be able to add to it in parent classes
		 */
		this.synthetics = new HashMap<IMethodSignature, Collection<ISyntheticMethod>>();

		for (ISyntheticMethod synthetic : builder.syntheticMethods()) {
			if (synthetic.getBackends().size() == 0) {
				addSynthetic(synthetic);
			} else {
				for (TypeMirror target : synthetic.getBackends()) {
					if (backend.compatibleWith(target)) {
						addSynthetic(synthetic);
						break;
					}
				}
			}
		}
	}

	protected void addSynthetic(ISyntheticMethod synthetic) {
		IMethodSignature signature = new MethodSignature(synthetic.getName(), synthetic.getParameters(), environment);

		Collection<ISyntheticMethod> similar = synthetics.get(signature);
		if (similar == null) synthetics.put(signature, similar = new ArrayList<ISyntheticMethod>());

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
	public Collection<IMethodBaked> getMethods() {
		return methods;
	}

	@Override
	public Map<IMethodSignature, Collection<ISyntheticMethod>> getSyntheticMethods() {
		return Collections.unmodifiableMap(synthetics);
	}

	@Override
	public Element getElement() {
		return element;
	}
}
