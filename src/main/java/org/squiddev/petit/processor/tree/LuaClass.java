package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;


/**
 * Data about the class we are generating
 */
public class LuaClass implements PeripheralClass {
	private final TypeElement klass;
	private final String name;
	private final Set<PeripheralMethod> methods;
	private final Environment environment;

	public LuaClass(String name, TypeElement klass, Environment environment) {
		this.name = name;
		this.klass = klass;
		this.environment = environment;

		// Gather methods
		Set<PeripheralMethod> methods = this.methods = new HashSet<PeripheralMethod>();
		for (Element element : klass.getEnclosedElements()) {
			if (element.getKind() == ElementKind.METHOD) {
				ExecutableElement method = (ExecutableElement) element;
				if (method.getAnnotation(LuaFunction.class) != null) {
					methods.add(new LuaMethod(this, method));
				}
			}
		}

		environment.getTransformer().transform(this);

		if (methods.size() == 0) {
			environment.getMessager().printMessage(Diagnostic.Kind.ERROR, "No @LuaFunction methods on this peripheral", klass);
		}
	}

	@Override
	public String getGeneratedName() {
		String[] fullName = getEnvironment().getElementUtils().getBinaryName(klass).toString().split("\\.");
		return fullName[fullName.length - 1].replace("$", "_") + "_Peripheral";
	}

	@Override
	public boolean process() {
		boolean success = true;
		Messager messager = getEnvironment().getMessager();

		Set<String> names = new HashSet<String>();
		for (PeripheralMethod method : methods) {
			for (String name : method.names()) {
				if (!names.add(name)) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate name '" + name + "'", method.getElement());
					success = false;
				}
			}
		}

		for (PeripheralMethod method : methods) {
			success &= method.process();
		}

		return success;
	}

	@Override
	public Environment getEnvironment() {
		return environment;
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
	public Set<PeripheralMethod> methods() {
		return methods;
	}
}
