package org.squiddev.petit.base.backend;

import org.squiddev.petit.api.backend.Backend;
import org.squiddev.petit.api.backend.InboundConverter;
import org.squiddev.petit.api.backend.OutboundConverter;
import org.squiddev.petit.api.tree.ArgumentKind;

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
