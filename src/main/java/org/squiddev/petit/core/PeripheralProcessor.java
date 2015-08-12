package org.squiddev.petit.core;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.annotation.LuaFunction;
import org.squiddev.petit.annotation.Peripheral;
import org.squiddev.petit.annotation.converter.Inbound;
import org.squiddev.petit.annotation.converter.Outbound;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.backend.InboundConverter;
import org.squiddev.petit.api.backend.OutboundConverter;
import org.squiddev.petit.api.backend.Segment;
import org.squiddev.petit.api.transformer.ITransformerContainer;
import org.squiddev.petit.api.tree.baked.IArgumentBaked;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.baked.IMethodBaked;
import org.squiddev.petit.api.tree.builder.IArgumentBuilder;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.api.tree.builder.IMethodBuilder;
import org.squiddev.petit.base.backend.AbstractInboundConverter;
import org.squiddev.petit.base.backend.AbstractOutboundConverter;
import org.squiddev.petit.base.transformer.TransformerContainer;
import org.squiddev.petit.base.tree.builder.ClassBuilder;
import org.squiddev.petit.core.backend.Utils;
import org.squiddev.petit.core.backend.iperipheral.IPeripheralBackend;
import org.squiddev.petit.core.transformer.BuilderVerifier;
import org.squiddev.petit.core.transformer.DefaultTransformers;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * The main processor for peripherals
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PeripheralProcessor extends AbstractProcessor {
	protected Environment environment;
	protected ITransformerContainer transformers;
	protected BuilderVerifier builderVerifier;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		environment = new BaseEnvironment(processingEnv);
		builderVerifier = new BuilderVerifier(environment);
		transformers = new TransformerContainer();
		DefaultTransformers.add(transformers, environment);
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

		transformers.verify(roundEnvironment);

		return true;
	}

	public void process(Element elem, Iterable<Backend> backends) {
		if (elem.getKind() != ElementKind.CLASS) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Peripheral", elem);
			return;
		}

		IClassBuilder builder;
		try {
			builder = new ClassBuilder(elem.getAnnotation(Peripheral.class).value(), (TypeElement) elem, environment);
			for (IMethodBuilder method : builder.methods()) {
				for (IArgumentBuilder argument : method.getArguments()) {
					transformers.transform(argument);
				}
				transformers.transform(method);
			}
			transformers.transform(builder);

			if (!builderVerifier.verify(builder)) return;
		} catch (Exception e) {
			StringWriter buffer = new StringWriter();
			e.printStackTrace(new PrintWriter(buffer));
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString(), elem);

			return;
		}

		for (Backend backend : backends) {
			try {
				IClassBaked baked = backend.bake(builder);
				if (!backend.getVerifier().verify(baked)) continue;
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

		for (Class annotation : transformers.getAnnotations()) {
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

	public void addInboundConverters(Iterable<Backend> backends, RoundEnvironment round) {
		for (Element element : round.getElementsAnnotatedWith(Inbound.class)) {
			final ExecutableElement method = getStaticMethod(element, Inbound.class);
			if (method == null) continue;

			List<? extends VariableElement> params = method.getParameters();
			if (params.size() != 1 || !environment.getTypeHelpers().isObject(params.get(0).asType())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must have one argument of type java.lang.Object", element);
				continue;
			}

			Object name = environment.getElementHelpers().getValue(element, Inbound.class, "value");
			InboundConverter converter = new AbstractInboundConverter(environment, name == null || ((String) name).isEmpty() ? method.getReturnType().toString() : (String) name) {
				@Override
				public Segment validate(IArgumentBaked argument, String from) {
					return new Segment(
						"($N = $T.$N($N)) != null",
						"arg_" + argument.getIndex(),
						method.getEnclosingElement(),
						method.getSimpleName(),
						from
					);
				}

				@Override
				public CodeBlock preamble(IArgumentBaked argument) {
					return CodeBlock.builder()
						.addStatement("$T $N", method.getReturnType(), "arg_" + argument.getIndex())
						.build();
				}

				@Override
				public CodeBlock convert(IArgumentBaked argument, String from) {
					return Utils.block("arg_" + argument.getIndex());
				}

				@Override
				public Iterable<TypeMirror> getTypes() {
					return Collections.singleton(method.getReturnType());
				}
			};

			Collection<TypeMirror> validBackends = environment.getElementHelpers().getTypeMirrors(element, Inbound.class, "backends");
			for (Backend backend : backends) {
				if (validBackends == null || validBackends.size() == 0) {
					backend.addInboundConverter(converter);
				} else {
					for (TypeMirror match : validBackends) {
						if (backend.compatibleWith(match)) {
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
				public CodeBlock convert(IMethodBaked baked, String from) {
					return Utils.block("$T.$N($N)", method.getEnclosingElement(), method.getSimpleName(), from);
				}
			};

			Collection<TypeMirror> validBackends = environment.getElementHelpers().getTypeMirrors(element, Outbound.class, "backends");
			for (Backend backend : backends) {
				if (validBackends == null || validBackends.size() == 0) {
					backend.addOutboundConverter(converter);
				} else {
					for (TypeMirror match : validBackends) {
						if (backend.compatibleWith(match)) {
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
