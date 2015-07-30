package org.squiddev.petit.api.compile.backend;

import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;

import javax.lang.model.type.TypeMirror;

/**
 * The base class for writes
 */
public interface Backend {
	ClassBaked bake(ClassBuilder builder, Environment environment);

	TypeSpec.Builder writeClass(ClassBaked klass);

	void addInboundConverter(InboundConverter converter);

	void addOutboundConverter(OutboundConverter converter);

	InboundConverter getInboundConverter(TypeMirror type);

	OutboundConverter getToConverter(TypeMirror type);
}
