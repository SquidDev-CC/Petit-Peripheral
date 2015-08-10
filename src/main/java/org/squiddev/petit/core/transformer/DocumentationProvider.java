package org.squiddev.petit.core.transformer;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.annotation.Document;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.TypeHelper;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.ArgumentKind;
import org.squiddev.petit.api.tree.baked.IClassBaked;
import org.squiddev.petit.api.tree.builder.IClassBuilder;
import org.squiddev.petit.base.transformer.AbstractAnnotationTransformer;
import org.squiddev.petit.base.tree.AbstractSyntheticMethod;
import org.squiddev.petit.base.tree.builder.ArgumentBuilder;
import org.squiddev.petit.base.tree.builder.MethodBuilder;
import org.squiddev.petit.core.backend.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;

public class DocumentationProvider extends AbstractAnnotationTransformer<Document> {
	public DocumentationProvider(Environment environment) {
		super(Document.class, environment);
	}

	@Override
	public void transform(IClassBuilder klass, Document annotation) {
		super.transform(klass, annotation);
		klass.methods().add(new DocumentationMethod(klass, environment));
		klass.syntheticMethods().add(new DocumentationSynthetic(klass.getElement(), environment.getTypeHelpers()));
	}

	protected static class DocumentationMethod extends MethodBuilder {
		protected final Environment environment;

		public DocumentationMethod(IClassBuilder klass, final Environment environment) {
			super(klass);
			this.environment = environment;

			arguments().add(new ArgumentBuilder(this, klass.getElement(), ArgumentKind.OPTIONAL) {
				@Override
				public TypeMirror getType() {
					return environment.getTypeHelpers().getMirror(String.class);
				}
			});
			names().add("getDocumentation");
		}

		@Override
		public String getTarget() {
			return "this.getDocumentation";
		}

		@Override
		public Element getElement() {
			return getParent().getElement();
		}

		@Override
		public TypeMirror getReturnType() {
			return environment.getTypeHelpers().getMirror(String.class);
		}
	}

	protected static class DocumentationSynthetic extends AbstractSyntheticMethod {
		public DocumentationSynthetic(Element element, TypeHelper helper) {
			super(Collections.<TypeMirror>emptyList(), "getDocumentation", Collections.singletonList(helper.getMirror(String.class)), helper.getMirror(String.class), element);
		}

		@Override
		public CodeBlock build(Backend backend, IClassBaked baked) {
			// TODO: Parse documentation
			return Utils.block("return null;");
		}
	}
}
