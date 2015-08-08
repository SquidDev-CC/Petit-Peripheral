package org.squiddev.petit.base.transformer;

import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.transformer.Transformer;
import org.squiddev.petit.api.tree.builder.ArgumentBuilder;
import org.squiddev.petit.api.tree.builder.ClassBuilder;
import org.squiddev.petit.api.tree.builder.MethodBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class AbstractTransformer<T extends Annotation> implements Transformer<T> {
	protected final Environment environment;

	public AbstractTransformer(Environment environment) {
		this.environment = environment;
	}


	@Override
	public void transform(ClassBuilder klass, T annotation) {
	}

	@Override
	public void transform(MethodBuilder method, T annotation) {
	}

	@Override
	public void transform(ArgumentBuilder argument, T annotation) {
	}

	@Override
	public boolean validate(Element target, T annotation) {
		boolean success = true;

		Class<?> base = annotation.getClass().getInterfaces()[0];
		Target t = base.getClass().getAnnotation(Target.class);
		List<ElementType> type;
		if (t == null) {
			type = Arrays.asList(ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD);
		} else {
			type = Arrays.asList(t.value());
		}

		if (!isValidType(target.getKind(), type)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.ERROR,
				"Unexpected @" + base.getSimpleName() + " on " + target.getKind() + ", expected " + type,
				target
			);
			success = false;
		}

		if (!hasPeripheral(target)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.WARNING,
				"Cannot find @Peripheral",
				target
			);
			success = false;
		}


		return success;
	}

	protected boolean isValidType(ElementKind kind, Collection<ElementType> type) {
		switch (kind) {
			case METHOD:
				return type.contains(ElementType.METHOD);
			case CLASS:
				return type.contains(ElementType.TYPE);
			case PARAMETER:
				return type.contains(ElementType.PARAMETER);
			default:
				return false;
		}
	}

	protected boolean hasPeripheral(Element element) {
		switch (element.getKind()) {
			case CLASS:
				return element.getAnnotation(Peripheral.class) != null;
			case METHOD:
			case PARAMETER:
				return hasPeripheral(element.getEnclosingElement());
			default:
				return false;
		}
	}
}
