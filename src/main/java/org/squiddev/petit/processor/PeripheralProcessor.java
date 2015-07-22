package org.squiddev.petit.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.api.LuaFunction;
import org.squiddev.petit.api.Peripheral;
import org.squiddev.petit.processor.tree.LuaClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
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
		environment = new Environment(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		try {
			Writer writer = new Writer();

			for (Element elem : roundEnvironment.getElementsAnnotatedWith(Peripheral.class)) {
				if (elem.getKind() != ElementKind.CLASS) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Peripheral", elem);
					return true;
				}

				try {
					LuaClass klass = new LuaClass(elem.getAnnotation(Peripheral.class).value(), (TypeElement) elem, environment);

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
		} catch (Exception e) {
			StringWriter buffer = new StringWriter();
			e.printStackTrace(new PrintWriter(buffer));
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, buffer.toString());
		}

		environment.transformer.validate(roundEnvironment);

		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new HashSet<String>();
		types.add(Peripheral.class.getName());
		types.add(LuaFunction.class.getName());

		for (Class<? extends Annotation> annotation : environment.transformer.annotations()) {
			types.add(annotation.getName());
		}

		return types;
	}
}
