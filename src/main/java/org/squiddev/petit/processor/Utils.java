package org.squiddev.petit.processor;

import com.squareup.javapoet.CodeBlock;

public final class Utils {
	private Utils() {
		throw new IllegalStateException("Cannot create Utils");
	}

	public static CodeBlock block(String format, Object... args) {
		return CodeBlock.builder().add(format, args).build();
	}
}
