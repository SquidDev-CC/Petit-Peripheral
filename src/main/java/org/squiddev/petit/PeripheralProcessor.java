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
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The main processor for peripherals
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PeripheralProcessor extends AbstractProcessor {
	protected Environment environment;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		environment = new BaseEnvironment(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		Set<Backend> backends = new HashSet<Backend>();
		backends.add(new IPeripheralBackend(environment));
		BuilderValidator builderValidator = new BuilderValidator();
		BakedValidator bakedValidator = new BakedValidator();

		Set<InboundConverter> inbound = getInboundConverters(roundEnvironment);
		Set<OutboundConverter> outbound = getOutboundConverters(roundEnvironment);

		for (Element elem : roundEnvironment.getElementsAnnotatedWith(Peripheral.class)) {
			if (elem.getKind() != ElementKind.CLASS) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Peripheral", elem);
				return true;
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

				if (!builderValidator.validate(builder, environment)) continue;
			} catch (Exception e) {
				StringWriter buffer = new StringWriter();
				e.printStackTrace(new PrintWriter(buffer));
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString(), elem);

				continue;
			}

			for (Backend backend : backends) {
				// Setup various converters
				// TODO: Add filters to specify a backend
				for (InboundConverter converter : inbound) {
					backend.addInboundConverter(converter);
				}
				for (OutboundConverter converter : outbound) {
					backend.addOutboundConverter(converter);
				}

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

		environment.getTransformer().validate(roundEnvironment);

		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new HashSet<String>();
		types.add(Peripheral.class.getName());
		types.add(LuaFunction.class.getName());

		for (java.lang.Class annotation : environment.getTransformer().annotations()) {
			types.add(annotation.getName());
		}

		return types;
	}

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

	public Set<InboundConverter> getInboundConverters(RoundEnvironment round) {
		Set<InboundConverter> converters = new HashSet<InboundConverter>();

		for (Element element : round.getElementsAnnotatedWith(Inbound.class)) {
			final ExecutableElement method = getStaticMethod(element, Inbound.class);
			if (method == null) continue;

			List<? extends VariableElement> params = method.getParameters();
			if (params.size() != 1 || !environment.getTypeHelpers().isObject(params.get(0).asType())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must have one argument of type java.lang.Object", element);
				continue;
			}

			converters.add(new AbstractInboundConverter(environment, method.getReturnType().toString()) {
				@Override
				public Segment validate(ArgumentBaked argument, String from) {
					return new Segment(
						"($N = $T.$N($N)) != null",
						argument.getElement().getSimpleName().toString() + "_arg",
						method.getEnclosingElement(),
						method.getSimpleName(),
						from
					);
				}

				@Override
				public CodeBlock preamble(ArgumentBaked argument) {
					return CodeBlock.builder()
						.addStatement("$T $N", method.getReturnType(), argument.getElement().getSimpleName().toString() + "_arg")
						.build();
				}

				@Override
				public CodeBlock convert(ArgumentBaked argument, String from) {
					return Utils.block(argument.getElement().getSimpleName().toString() + "_arg");
				}

				@Override
				public Iterable<TypeMirror> getTypes() {
					return Collections.singleton(method.getReturnType());
				}
			});
		}

		return converters;
	}

	public Set<OutboundConverter> getOutboundConverters(RoundEnvironment round) {
		Set<OutboundConverter> converters = new HashSet<OutboundConverter>();
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

			converters.add(new AbstractOutboundConverter(environment, params.get(0).asType()) {
				@Override
				public CodeBlock convertTo(String from) {
					return Utils.block("$T.$N($N)", method.getEnclosingElement(), method.getSimpleName(), from);
				}
			});
		}

		return converters;
	}
}
