package org.squiddev.petit;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.Peripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.OutboundConverter;
import org.squiddev.petit.api.compile.backend.Segment;
import org.squiddev.petit.api.compile.backend.tree.ArgumentBaked;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.transformer.tree.ArgumentBuilder;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.transformer.tree.MethodBuilder;
import org.squiddev.petit.api.runtime.Inbound;
import org.squiddev.petit.api.runtime.Outbound;
import org.squiddev.petit.backend.IPeripheralBackend;
import org.squiddev.petit.backend.Utils;
import org.squiddev.petit.backend.converter.inbound.AbstractInboundConverter;
import org.squiddev.petit.backend.converter.outbound.AbstractOutboundConverter;
import org.squiddev.petit.backend.tree.BakedValidator;
import org.squiddev.petit.compile.BaseEnvironment;
import org.squiddev.petit.transformer.tree.BasicClassBuilder;
import org.squiddev.petit.transformer.tree.BuilderValidator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * The main processor for peripherals
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PeripheralProcessor extends AbstractProcessor {
	protected Environment environment;
	protected BuilderValidator builderValidator = new BuilderValidator();
	protected BakedValidator bakedValidator = new BakedValidator();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		environment = new BaseEnvironment(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		Collection<Backend> backends = new HashSet<Backend>();
		backends.add(new IPeripheralBackend(environment));
		addInboundConverters(backends, roundEnvironment);
		addOutboundConverters(backends, roundEnvironment);

		for (Element elem : roundEnvironment.getElementsAnnotatedWith(Peripheral.class)) {
			process(elem, backends);
		}

		environment.getTransformer().validate(roundEnvironment);

		return true;
	}

	public void process(Element elem, Iterable<Backend> backends) {
		if (elem.getKind() != ElementKind.CLASS) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Peripheral", elem);
			return;
		}

		ClassBuilder builder;
		try {
			builder = new BasicClassBuilder(elem.getAnnotation(Peripheral.class).value(), (TypeElement) elem);
			for (MethodBuilder method : builder.methods()) {
				for (ArgumentBuilder argument : method.getArguments()) {
					environment.getTransformer().transform(argument);
				}
				environment.getTransformer().transform(method);
			}
			environment.getTransformer().transform(builder);

			if (!builderValidator.validate(builder, environment)) return;
		} catch (Exception e) {
			StringWriter buffer = new StringWriter();
			e.printStackTrace(new PrintWriter(buffer));
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString(), elem);

			return;
		}

		for (Backend backend : backends) {
			try {
				ClassBaked baked = backend.bake(builder, environment);
				if (!bakedValidator.validate(baked, environment, backend)) continue;
				TypeSpec spec = backend.writeClass(baked).build();

				JavaFile
					.builder(processingEnv.getElementUtils().getPackageOf(elem).getQualifiedName().toString(), spec)
					.build()
					.writeTo(processingEnv.getFiler());
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "[" + backend.toString() + "]: " + e.toString(), elem);
			} catch (Exception e) {
				StringWriter buffer = new StringWriter().append("[").append(backend.toString()).append("]: ");
				e.printStackTrace(new PrintWriter(buffer));
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString(), elem);
			}
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new HashSet<String>();
		types.add(Peripheral.class.getName());
		types.add(LuaFunction.class.getName());
		types.add(Inbound.class.getName());
		types.add(Outbound.class.getName());

		for (Class annotation : environment.getTransformer().annotations()) {
			types.add(annotation.getName());
		}

		return types;
	}

	//region Converter finding
	public ExecutableElement getStaticMethod(Element element, java.lang.Class annotation) {
		if (element.getKind() != ElementKind.METHOD) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only methods can be annotated with @" + annotation.getSimpleName(), element);
			return null;
		}
		ExecutableElement method = (ExecutableElement) element;
		if (!method.getModifiers().contains(Modifier.STATIC) || !method.getModifiers().contains(Modifier.PUBLIC)) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must be public and static", element);
			return null;
		}

		return method;
	}

	@SuppressWarnings("unchecked")
	public Collection<TypeMirror> getTypeMirrors(Element element, Class<? extends Annotation> annotation, String name) {
		String annotationName = annotation.getName();
		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			DeclaredType annotationType = annotationMirror.getAnnotationType();
			TypeElement annotationElement = (TypeElement) annotationType.asElement();

			if (annotationElement.getQualifiedName().contentEquals(annotationName)) {
				for (Map.Entry<? extends ExecutableElement, AnnotationValue> entry : Collections.unmodifiableMap(annotationMirror.getElementValues()).entrySet()) {
					if (entry.getKey().getSimpleName().contentEquals(name)) {
						Collection<AnnotationValue> values = (Collection<AnnotationValue>) entry.getValue().getValue();
						List<TypeMirror> mirrors = new ArrayList<TypeMirror>(values.size());
						for (AnnotationValue value : values) {
							mirrors.add((TypeMirror) value.getValue());
						}
						return mirrors;
					}
				}
			}
		}
		return null;
	}

	public void addInboundConverters(Iterable<Backend> backends, RoundEnvironment round) {
		for (Element element : round.getElementsAnnotatedWith(Inbound.class)) {
			final ExecutableElement method = getStaticMethod(element, Inbound.class);
			if (method == null) continue;

			List<? extends VariableElement> params = method.getParameters();
			if (params.size() != 1 || !environment.getTypeHelpers().isObject(params.get(0).asType())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must have one argument of type java.lang.Object", element);
				continue;
			}

			InboundConverter converter = new AbstractInboundConverter(environment, method.getReturnType().toString()) {
				@Override
				public Segment validate(ArgumentBaked argument, String from) {
					return new Segment(
						"($N = $T.$N($N)) != null",
						"arg_" + argument.getIndex(),
						method.getEnclosingElement(),
						method.getSimpleName(),
						from
					);
				}

				@Override
				public CodeBlock preamble(ArgumentBaked argument) {
					return CodeBlock.builder()
						.addStatement("$T $N", method.getReturnType(), "arg_" + argument.getIndex())
						.build();
				}

				@Override
				public CodeBlock convert(ArgumentBaked argument, String from) {
					return Utils.block("arg_" + argument.getIndex());
				}

				@Override
				public Iterable<TypeMirror> getTypes() {
					return Collections.singleton(method.getReturnType());
				}
			};

			Collection<TypeMirror> validBackends = getTypeMirrors(element, Inbound.class, "backends");
			for (Backend backend : backends) {
				if (validBackends == null || validBackends.size() == 0) {
					backend.addInboundConverter(converter);
				} else {
					for (TypeMirror match : validBackends) {
						if (backend.compatibleWith(match, environment)) {
							backend.addInboundConverter(converter);
							break;
						}
					}
				}
			}
		}
	}

	public void addOutboundConverters(Iterable<Backend> backends, RoundEnvironment round) {
		for (Element element : round.getElementsAnnotatedWith(Outbound.class)) {
			final ExecutableElement method = getStaticMethod(element, Outbound.class);
			if (method == null) continue;

			final List<? extends VariableElement> params = method.getParameters();

			boolean success = true;
			if (params.size() != 1) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must have one argument", element);
				success = false;
			}

			if (!environment.getTypeHelpers().isObjectArray(method.getReturnType())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must return java.lang.Object[]", element);
				success = false;
			}

			if (!success) continue;

			OutboundConverter converter = new AbstractOutboundConverter(environment, params.get(0).asType()) {
				@Override
				public CodeBlock convertTo(String from) {
					return Utils.block("$T.$N($N)", method.getEnclosingElement(), method.getSimpleName(), from);
				}
			};

			Collection<TypeMirror> validBackends = getTypeMirrors(element, Outbound.class, "backends");
			for (Backend backend : backends) {
				if (validBackends == null || validBackends.size() == 0) {
					backend.addOutboundConverter(converter);
				} else {
					for (TypeMirror match : validBackends) {
						if (backend.compatibleWith(match, environment)) {
							backend.addOutboundConverter(converter);
							break;
						}
					}
				}
			}
		}
	}
	//endregion
}
