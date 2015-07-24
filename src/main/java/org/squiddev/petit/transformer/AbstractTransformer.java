package org.squiddev.petit.transformer;

import org.squiddev.petit.api.Peripheral;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.compile.tree.PeripheralMethod;

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
	protected final org.squiddev.petit.api.compile.Environment environment;

	public AbstractTransformer(org.squiddev.petit.api.compile.Environment environment) {
		this.environment = environment;
	}


	@Override
	public void transform(PeripheralClass klass, T annotation) {
	}

	@Override
	public void transform(PeripheralMethod method, T annotation) {
	}

	@Override
	public void transform(Argument argument, T annotation) {
	}

	@Override
	public boolean validate(Element target, T annotation) {
		boolean success = true;

		Target t = annotation.getClass().getAnnotation(Target.class);
		List<ElementType> type;
		if (t == null) {
			type = Arrays.asList(ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD);
		} else {
			type = Arrays.asList(t.value());
		}

		if (!isValidType(target.getKind(), type)) {
			environment.getMessager().printMessage(
				Diagnostic.Kind.ERROR,
				"Unexpected @" + annotation.getClass().getSimpleName() + " on " + target.getKind() + ", expected on" + type,
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
				return type.contains(ElementType.PACKAGE);
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
