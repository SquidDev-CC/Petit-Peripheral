package org.squiddev.petit.backend.iperipheral;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.squiddev.petit.api.compile.Environment;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.OutboundConverter;
import org.squiddev.petit.api.compile.backend.tree.ClassBaked;
import org.squiddev.petit.api.compile.transformer.tree.ClassBuilder;
import org.squiddev.petit.api.compile.tree.Validator;
import org.squiddev.petit.backend.converter.inbound.AbstractInboundConverter;
import org.squiddev.petit.backend.converter.inbound.PrimitiveTypeConverter;
import org.squiddev.petit.backend.converter.inbound.ProvidedConverter;
import org.squiddev.petit.backend.converter.outbound.SimpleConverter;

import javax.lang.model.type.TypeMirror;
import java.util.Collections;

public class IPeripheralBackend extends IPeripheralWriter {
	private final Validator<ClassBaked> validator = new IPeripheralValidator(this, environment);

	public IPeripheralBackend(Environment environment) {
		super(environment);

		addInboundConverter(new AbstractInboundConverter(environment, "anything") {
			@Override
			public Iterable<TypeMirror> getTypes() {
				return Collections.singleton(environment.getTypeHelpers().object());
			}
		});

		for (InboundConverter converter : PrimitiveTypeConverter.add(environment)) {
			addInboundConverter(converter);
		}

		addInboundConverter(new ProvidedConverter(environment, IComputerAccess.class, ARG_COMPUTER));
		addInboundConverter(new ProvidedConverter(environment, ILuaContext.class, ARG_LUA_CONTEXT));

		for (OutboundConverter converter : SimpleConverter.addBasics(environment)) {
			addOutboundConverter(converter);
		}
	}

	@Override
	public ClassBaked bake(ClassBuilder builder) {
		String[] fullName = environment.getElementUtils().getBinaryName(builder.getElement()).toString().split("\\.");
		return new IPeripheralBaked(fullName[fullName.length - 1].replace("$", "_") + "_Peripheral", builder, this, environment);
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
