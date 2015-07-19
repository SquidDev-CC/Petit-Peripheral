package org.squiddev.petit.conversion.from;

import org.squiddev.petit.processor.Segment;

/**
 * Simply checks if it is an instance of the class and casts it to it.
 */
public class InstanceofConverter implements FromLuaConverter {
	private final Class<?> type;
	private final String name;

	public InstanceofConverter(Class<?> type) {
		this(type, type.getSimpleName());
	}

	public InstanceofConverter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public Segment convertFrom(String fromToken, String toToken) {
		return new Segment("$N instanceof $T && ($N = ($T)$N) != null", fromToken, type, toToken, type, fromToken);
	}

	@Override
	public String getName() {
		return name;
	}
}
