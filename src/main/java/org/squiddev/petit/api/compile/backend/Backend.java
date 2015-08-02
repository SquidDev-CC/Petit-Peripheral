package org.squiddev.petit.api.compile.backend;

import com.squareup.javapoet.TypeSpec;
import org.squiddev.petit.api.compile.ArgumentKind;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;

import javax.lang.model.type.TypeMirror;

/**
 * The base class for writes
 */
public interface Backend {
	/**
	 * The name of the 'instance' field - the field
	 * the target object is bound to
	 */
	String FIELD_INSTANCE = "instance";

	/**
	 * Convert a class builder into a backed class.
	 *
	 * The baked class has backend specific behaviour
	 *
	 * @param builder     The builder to bake
	 * @param environment The current environment (TODO: Remove this dependency).
	 * @return The baked class
	 */
	ClassBaked bake(ClassBuilder builder, Environment environment);

	/**
	 * Compile a baked class
	 *
	 * The baked class must have been baked by this backend,
	 * otherwise things may explode.
	 *
	 * @param baked The class to compile
	 * @return The source of the class
	 */
	TypeSpec.Builder writeClass(ClassBaked baked);

	/**
	 * Add an inbound converter
	 *
	 * @param converter The converter to add
	 */
	void addInboundConverter(InboundConverter converter);

	/**
	 * Add an outbound converter
	 *
	 * @param converter The converter to add
	 */
	void addOutboundConverter(OutboundConverter converter);

	/**
	 * Get a converter for a type
	 *
	 * @param kind The argument kind
	 * @param type The type to get the converter for
	 * @return The converter or {@code null} if none found
	 */
	InboundConverter getInboundConverter(ArgumentKind kind, TypeMirror type);

	/**
	 * Get a converter for an type
	 *
	 * @param type The type to get the converter for
	 * @return The converter or {@code null} if none found
	 */
	OutboundConverter getToConverter(TypeMirror type);

	/**
	 * Check if this backend is compatible with this type.
	 *
	 * This probably only checks if the type extends
	 * whatever this backend bases it off (i.e. {@link dan200.computercraft.api.peripheral.IPeripheral})
	 *
	 * @param type        The type to check
	 * @param environment The current environment
	 * @return If this type is compatible
	 */
	boolean compatibleWith(TypeMirror type, Environment environment);
}
