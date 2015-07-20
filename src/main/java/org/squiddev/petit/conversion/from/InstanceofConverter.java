package org.squiddev.petit.conversion.from;

import org.squiddev.petit.processor.Environment;
import org.squiddev.petit.processor.Segment;

import javax.lang.model.type.TypeMirror;

/**
 * Simply checks if it is an instance of the class and casts it to it.
 */
public class InstanceofConverter extends AbstractFromLuaConverter {
	private final String name;

	public InstanceofConverter(Environment env, TypeMirror type, String name) {
		super(env, type);
		this.name = name;
	}

	public InstanceofConverter(Environment env, Class<?> type, String name) {
		super(env, type);
		this.name = name;
	}


	@Override
	public Segment validate(String from, String temp) {
		return new Segment("$N instanceof $T", from, type);
	}

	@Override
	public Segment getValue(String from, String temp) {
		return new Segment("($T)$N", type, from);
	}

	@Override
	public String getName() {
		return name;
	}
}
