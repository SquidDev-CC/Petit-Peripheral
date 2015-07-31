package org.squiddev.petit.backend;

import org.squiddev.petit.api.compile.ArgumentKind;
import org.squiddev.petit.api.compile.backend.Backend;
import org.squiddev.petit.api.compile.backend.InboundConverter;
import org.squiddev.petit.api.compile.backend.OutboundConverter;

import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBackend implements Backend {
	protected final Set<InboundConverter> fromConverters = new HashSet<InboundConverter>();
	protected final Set<OutboundConverter> toConverters = new HashSet<OutboundConverter>();

	@Override
	public void addInboundConverter(InboundConverter converter) {
		fromConverters.add(converter);
	}

	@Override
	public void addOutboundConverter(OutboundConverter converter) {
		toConverters.add(converter);
	}

	@Override
	public InboundConverter getInboundConverter(ArgumentKind kind, TypeMirror type) {
		for (InboundConverter converter : fromConverters) {
			if (converter.matches(kind, type)) return converter;
		}
		return null;
	}

	@Override
	public OutboundConverter getToConverter(TypeMirror type) {
		for (OutboundConverter converter : toConverters) {
			if (converter.matches(type)) return converter;
		}
		return null;
	}
}
