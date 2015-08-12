package org.squiddev.petit.annotation;


import java.lang.annotation.*;

/**
 * Mark a class as extending/implementing other types.
 *
 * Whilst the annotation is inherited, the contents of it are not.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Extends {
	/**
	 * Classes to extend
	 *
	 * @return The classes to extend
	 */
	Class<?>[] value();
}
