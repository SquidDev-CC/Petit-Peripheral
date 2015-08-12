package org.squiddev.petit.annotation;

import java.lang.annotation.*;

/**
 * Mark a peripheral as providing documentation.
 * This is extracted directly from the javadoc of a method/class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Document {
}
