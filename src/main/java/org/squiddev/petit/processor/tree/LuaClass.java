package org.squiddev.petit.processor.tree;

import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.processor.Environment;

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
public class LuaClass {
	/**
	 * The class we are generating method from
	 */
	public final TypeElement klass;

	/**
	 * Names this API should be bound to
	 *
	 * @see org.squiddev.petit.api.Peripheral#value()
	 */
	public final String name;

	/**
	 * List of methods this class will have
	 */
	public final Set<LuaMethod> methods;

	/**
	 * The transformer this class uses
	 */
	public final Environment environment;

	public LuaClass(String name, TypeElement klass, Environment environment) {
		this.name = name;
		this.klass = klass;
		this.environment = environment;

		// Gather methods
		Set<LuaMethod> methods = this.methods = new HashSet<LuaMethod>();
		for (Element element : klass.getEnclosedElements()) {
			if (element.getKind() == ElementKind.METHOD) {
				ExecutableElement method = (ExecutableElement) element;
				if (method.getAnnotation(LuaFunction.class) != null) {
					methods.add(new LuaMethod(this, method));
				}
			}
		}

		environment.transformer.transform(this);

		if (methods.size() == 0) {
			environment.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "No @LuaFunction methods on this peripheral", klass);
		}
	}

	public String getGeneratedName() {
		String[] fullName = environment.processingEnvironment.getElementUtils().getBinaryName(klass).toString().split("\\.");
		return fullName[fullName.length - 1].replace("$", "_") + "_Peripheral";
	}

	public boolean process() {
		boolean success = true;
		Messager messager = environment.processingEnvironment.getMessager();

		Set<String> names = new HashSet<String>();
		for (LuaMethod method : methods) {
			for (String name : method.names) {
				if (!names.add(name)) {
					messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate name '" + name + "'", method.method);
					success = false;
				}
			}
		}

		for (LuaMethod method : methods) {
			success &= method.process();
		}

		return success;
	}
}
