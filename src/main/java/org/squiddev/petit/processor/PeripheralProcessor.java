package org.squiddev.petit.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.Peripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.Segment;
import org.squiddev.petit.api.compile.converter.ConverterContainer;
import org.squiddev.petit.api.compile.tree.Argument;
import org.squiddev.petit.api.compile.tree.PeripheralClass;
import org.squiddev.petit.api.runtime.Extracts;
import org.squiddev.petit.api.runtime.ToLua;
import org.squiddev.petit.conversion.from.AbstractFromLuaConverter;
import org.squiddev.petit.conversion.to.AbstractToLuaConverter;
import org.squiddev.petit.processor.tree.LuaClass;

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
import java.lang.annotation.Annotation;
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
		populateConverters(roundEnvironment);
		Writer writer = new Writer();

		for (Element elem : roundEnvironment.getElementsAnnotatedWith(Peripheral.class)) {
			if (elem.getKind() != ElementKind.CLASS) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Peripheral", elem);
				return true;
			}

			try {
				PeripheralClass klass = new LuaClass(elem.getAnnotation(Peripheral.class).value(), (TypeElement) elem, environment);

				if (!klass.process()) continue;

				TypeSpec spec = writer.writeClass(klass).build();
				try {
					JavaFile
						.builder(processingEnv.getElementUtils().getPackageOf(elem).getQualifiedName().toString(), spec)
						.build()
						.writeTo(processingEnv.getFiler());
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error " + e.toString(), elem);
				}
			} catch (Exception e) {
				StringWriter buffer = new StringWriter();
				e.printStackTrace(new PrintWriter(buffer));
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString(), elem);
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

		for (Class<? extends Annotation> annotation : environment.getTransformer().annotations()) {
			types.add(annotation.getName());
		}

		return types;
	}

	public ExecutableElement getStaticMethod(Element element, String annotation) {
		if (element.getKind() != ElementKind.METHOD) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only methods can be annotated with @" + annotation, element);
			return null;
		}
		ExecutableElement method = (ExecutableElement) element;
		if (!method.getModifiers().contains(Modifier.STATIC) || !method.getModifiers().contains(Modifier.PUBLIC)) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must be public and static", element);
			return null;
		}

		return method;
	}

	public void populateConverters(RoundEnvironment round) {
		ConverterContainer container = environment.getConverter();

		for (Element element : round.getElementsAnnotatedWith(Extracts.class)) {
			final ExecutableElement method = getStaticMethod(element, "Extracts");
			if (method == null) continue;

			List<? extends VariableElement> params = method.getParameters();
			if (params.size() != 1 || !environment.getTypeHelpers().isObject(params.get(0).asType())) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Must have one argument of type java.lang.Object", element);
				continue;
			}

			container.addFromConverter(new AbstractFromLuaConverter(environment, method.getReturnType().toString()) {
				@Override
				public Segment validate(Argument argument, String from) {
					return new Segment(
						"($N = $T.$N($N)) != null",
						argument.getElement().getSimpleName().toString() + "_arg",
						method.getEnclosingElement(),
						method.getSimpleName(),
						from
					);
				}

				@Override
				public CodeBlock preamble(Argument argument) {
					return CodeBlock.builder()
						.addStatement("$T $N", method.getReturnType(), argument.getElement().getSimpleName().toString() + "_arg")
						.build();
				}

				@Override
				public CodeBlock convert(Argument argument, String from) {
					return Utils.block(argument.getElement().getSimpleName().toString() + "_arg");
				}

				@Override
				public Iterable<TypeMirror> getTypes() {
					return Collections.singleton(method.getReturnType());
				}
			});
		}

		for (Element element : round.getElementsAnnotatedWith(ToLua.class)) {
			final ExecutableElement method = getStaticMethod(element, "ToLua");
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

			container.addToConverter(new AbstractToLuaConverter(environment, params.get(0).asType()) {
				@Override
				public CodeBlock convertTo(String from) {
					return Utils.block("$T.$N($N)", method.getEnclosingElement(), method.getSimpleName(), from);
				}
			});
		}
	}
}
