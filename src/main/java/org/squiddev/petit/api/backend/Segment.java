package org.squiddev.petit.api.backend;

import com.squareup.javapoet.CodeBlock;

/**
 * A {@link CodeBlock} with additional metadata information
 */
public class Segment {
	private final boolean isStatement;
	private final CodeBlock block;

	public Segment(CodeBlock block) {
		this(block, false);
	}

	public Segment(CodeBlock block, boolean isStatement) {
		this.isStatement = isStatement;
		this.block = block;
	}

	public Segment(String contents, Object... args) {
		this(CodeBlock.builder().add(contents, args).build(), false);
	}

	/**
	 * Get the code for this segment
	 *
	 * @return The contents of this segment.
	 */
	public CodeBlock getCodeBlock() {
		return block;
	}

	/**
	 * If this segment is a statement (or list of statements).
	 *
	 * @return If this is a statement
	 */
	public boolean isStatement() {
		return isStatement;
	}
}
