package org.squiddev.petit.processor;

/**
 * Represents a code segment.
 */
public class Segment {
	public final String contents;

	public final Object[] values;

	public Segment(String contents, Object... values) {
		this.contents = contents;
		this.values = values;
	}
}
