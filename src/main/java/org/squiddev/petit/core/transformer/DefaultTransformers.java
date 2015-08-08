package org.squiddev.petit.core.transformer;

import org.squiddev.petit.annotation.Alias;
import org.squiddev.petit.annotation.Handler;
import org.squiddev.petit.annotation.Optional;
import org.squiddev.petit.annotation.Provided;
import org.squiddev.petit.api.ElementHelper;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.transformer.ITransformerContainer;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.ISyntheticMethod;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;
import org.squiddev.petit.base.transformer.AbstractAnnotationTransformer;
import org.squiddev.petit.base.transformer.AbstractTransformer;
import org.squiddev.petit.base.tree.SyntheticMethod;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;


/**
 * A series of useful transformers
 */
public final class DefaultTransformers {
	private DefaultTransformers() {
		throw new IllegalStateException("Cannot create this class");
	}

	public static void add(ITransformerContainer transformer, Environment environment) {
		transformer.add(new AbstractAnnotationTransformer<Alias>(Alias.class, environment) {
			@Override
			public void transform(IMethodBuilder target, Alias annotation) {
				String[] names = annotation.value();
				if (names == null) return;
				Collections.addAll(target.names(), names);
			}
		});

		transformer.add(new AbstractAnnotationTransformer<Optional>(Optional.class, environment) {
			@Override
			public void transform(IArgumentBuilder argument, Optional annotation) {
				argument.setKind(ArgumentKind.OPTIONAL);
			}
		});

		transformer.add(new AbstractAnnotationTransformer<Provided>(Provided.class, environment) {
			@Override
			public void transform(IArgumentBuilder argument, Provided annotation) {
				argument.setKind(ArgumentKind.PROVIDED);
			}
		});

		transformer.add(new AbstractTransformer(environment) {
			@Override
			public void transform(IClassBuilder klass) {
				super.transform(klass);
				if (klass.getElement() == null) return;

				ElementHelper helpers = environment.getElementHelpers();

				for (Element enclosed : klass.getElement().getEnclosedElements()) {
					Object backend;
					if (enclosed.getKind() == ElementKind.METHOD && (backend = helpers.getValue(enclosed, Handler.class, "value")) instanceof TypeMirror) {
						ExecutableElement element = (ExecutableElement) enclosed;

						SyntheticMethod.Builder builder = new SyntheticMethod.Builder(element.getSimpleName().toString(), element, environment)
							.addBackends((TypeMirror) backend)
							.returns(element.getReturnType())
							.addCode("$[");

						if (element.getReturnType().getKind() != TypeKind.VOID) {
							builder.addCode("return ");
						}
						builder.addCode("$N.$N(", Backend.FIELD_INSTANCE, element.getSimpleName());

						int index = 0;
						for (VariableElement arg : element.getParameters()) {
							builder.addParameters(arg.asType());

							if (index > 0) builder.addCode(", ");
							builder.addCode(ISyntheticMethod.ARG_PREFIX + index);
							index++;
						}

						klass.syntheticMethods().add(builder.addCode(");\n$]").build());
					}
				}
			}

			@Override
			public boolean validate(RoundEnvironment round) {
				boolean success = super.validate(round);

				Messager messager = environment.getMessager();
				ElementHelper helpers = environment.getElementHelpers();

				for (Element element : round.getElementsAnnotatedWith(Handler.class)) {
					if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
						messager.printMessage(Diagnostic.Kind.ERROR, "@Handler must be a child of a class", element);
						success = false;
					}
					if (element.getKind() != ElementKind.METHOD) {
						messager.printMessage(Diagnostic.Kind.ERROR, "@Handler must be on a method", element);
						success = false;
						continue;
					}

					Object backendValue;

					if (!((backendValue = helpers.getValue(element, Handler.class, "value")) instanceof TypeMirror)) {
						messager.printMessage(Diagnostic.Kind.ERROR, "@Handler must specify backend", element);
						success = false;
						continue;
					}

					if (((TypeMirror) backendValue).getKind() != TypeKind.DECLARED) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Backend must be a class", element);
						success = false;
						continue;
					}

					ExecutableElement method = (ExecutableElement) element;
					ExecutableElement other = helpers.getMethod((TypeElement) ((DeclaredType) backendValue).asElement(), method);

					if (other == null) {
						messager.printMessage(Diagnostic.Kind.ERROR, "Cannot find method " + method.getSimpleName(), element);
						success = false;
					}
				}

				return success;
			}

			@Override
			public Collection<Class<? extends Annotation>> getAnnotations() {
				return Collections.<Class<? extends Annotation>>singleton(Handler.class);
			}
		});
	}
}
