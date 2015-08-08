package org.squiddev.petit.base.tree;

import com.squareup.javapoet.CodeBlock;
import org.squiddev.petit.api.Environment;
import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.tree.MethodSignature;
import org.squiddev.petit.api.tree.baked.ClassBaked;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class BasicSyntheticMethod extends AbstractSyntheticMethod {
	private final CodeBlock codeBlock;

	public BasicSyntheticMethod(Collection<TypeMirror> backends, String name, List<TypeMirror> parameters, TypeMirror returnType, Element element, CodeBlock codeBlock) {
		super(backends, name, parameters, returnType, element);
		this.codeBlock = codeBlock;
	}

	public BasicSyntheticMethod(Collection<TypeMirror> backends, MethodSignature signature, TypeMirror returnType, Element element, CodeBlock codeBlock) {
		super(backends, signature, returnType, element);
		this.codeBlock = codeBlock;
	}

	@Override
	public CodeBlock build(Backend backend, ClassBaked baked) {
		return codeBlock;
	}

	public static class Builder {
		private final Environment environment;
		private final Element element;

		private final CodeBlock.Builder code = CodeBlock.builder();
		private final List<TypeMirror> parameters = new ArrayList<TypeMirror>();
		private final Collection<TypeMirror> backends = new HashSet<TypeMirror>();
		private final String name;
		private TypeMirror returns;

		public Builder(String name, Element element, Environment environment) {
			this.environment = environment;
			this.element = element;
			this.name = name;
			this.returns = environment.getTypeUtils().getNoType(TypeKind.VOID);
		}

		public Builder addBackends(TypeMirror... mirrors) {
			Collections.addAll(backends, mirrors);
			return this;
		}

		public Builder addBackends(Class<?>... mirrors) {
			for (Class<?> mirror : mirrors) {
				backends.add(environment.getTypeHelpers().getMirror(mirror));
			}
			return this;
		}

		public Builder addParameters(TypeMirror... mirrors) {
			Collections.addAll(parameters, mirrors);
			return this;
		}

		public Builder addParameters(Class<?>... mirrors) {
			for (Class<?> mirror : mirrors) {
				parameters.add(environment.getTypeHelpers().getMirror(mirror));
			}
			return this;
		}

		public Builder returns(TypeMirror type) {
			returns = type;
			return this;
		}

		public Builder returns(Class<?> type) {
			returns = environment.getTypeHelpers().getMirror(type);
			return this;
		}

		public Builder addCode(String format, Object... args) {
			code.add(format, args);
			return this;
		}

		public Builder addCode(CodeBlock codeBlock) {
			code.add(codeBlock);
			return this;
		}

		public Builder beginControlFlow(String controlFlow, Object... args) {
			code.beginControlFlow(controlFlow, args);
			return this;
		}

		public Builder nextControlFlow(String controlFlow, Object... args) {
			code.nextControlFlow(controlFlow, args);
			return this;
		}

		public Builder endControlFlow() {
			code.endControlFlow();
			return this;
		}

		public Builder endControlFlow(String controlFlow, Object... args) {
			code.endControlFlow(controlFlow, args);
			return this;
		}

		public Builder addStatement(String format, Object... args) {
			code.addStatement(format, args);
			return this;
		}

		public BasicSyntheticMethod build() {
			return new BasicSyntheticMethod(
				backends,
				name,
				parameters,
				returns,
				element,
				code.build()
			);
		}
	}
}
