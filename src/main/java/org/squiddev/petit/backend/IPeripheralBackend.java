package org.squiddev.petit.backend;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.Validator;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.backend.converter.inbound.AbstractInboundConverter;
import org.squiddev.petit.backend.converter.inbound.InstanceofConverter;
import org.squiddev.petit.backend.converter.inbound.PrimitiveTypeConverter;
import org.squiddev.petit.backend.converter.inbound.ProvidedConverter;
import org.squiddev.petit.backend.converter.outbound.SimpleConverter;
import org.squiddev.petit.backend.tree.BakedValidator;
import org.squiddev.petit.backend.tree.BasicClassBaked;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;

public class IPeripheralBackend extends IPeripheralWriter {
	private final Validator<ClassBaked> validator = new BakedValidator(this, environment);

	public IPeripheralBackend(Environment environment) {
		super(environment);

		addInboundConverter(new AbstractInboundConverter(environment, "anything") {
			@Override
			public Iterable<TypeMirror> getTypes() {
				return Collections.singleton(environment.getTypeHelpers().object());
			}
		});

		for (TypeKind type : new TypeKind[]{TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.FLOAT, TypeKind.DOUBLE}) {
			addInboundConverter(new PrimitiveTypeConverter.NumberConverter(environment, type));
		}
		addInboundConverter(new PrimitiveTypeConverter(environment, TypeKind.BOOLEAN, "boolean"));
		addInboundConverter(new PrimitiveTypeConverter.CharConverter(environment));
		addInboundConverter(new InstanceofConverter(environment, String.class, "string"));

		addInboundConverter(new ProvidedConverter(environment, IComputerAccess.class, ARG_COMPUTER));
		addInboundConverter(new ProvidedConverter(environment, ILuaContext.class, ARG_LUA_CONTEXT));

		addOutboundConverter(new SimpleConverter(environment, byte.class));
		addOutboundConverter(new SimpleConverter(environment, short.class));
		addOutboundConverter(new SimpleConverter(environment, int.class));
		addOutboundConverter(new SimpleConverter(environment, long.class));
		addOutboundConverter(new SimpleConverter(environment, float.class));
		addOutboundConverter(new SimpleConverter(environment, double.class));
		addOutboundConverter(new SimpleConverter(environment, String.class));
		addOutboundConverter(new SimpleConverter(environment, Object.class));
	}

	@Override
	public ClassBaked bake(ClassBuilder builder) {
		String[] fullName = environment.getElementUtils().getBinaryName(builder.getElement()).toString().split("\\.");
		return new BasicClassBaked(fullName[fullName.length - 1].replace("$", "_") + "_Peripheral", builder, this);
	}

	@Override
	public boolean compatibleWith(TypeMirror type) {
		return environment.getTypeUtils().isAssignable(environment.getTypeHelpers().getMirror(IPeripheral.class), type);
	}

	@Override
	public Validator<ClassBaked> getValidator() {
		return validator;
	}
}
