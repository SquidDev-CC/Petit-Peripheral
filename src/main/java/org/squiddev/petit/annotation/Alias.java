package org.squiddev.petit.annotation;

import java.lang.annotation.*;

/**
 * Defines a series of aliases for a function
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Alias {
	/**
	 * The extra names of the {@link LuaFunction}
	 *
	 * @return The names of this function
	 */
	String[] value();
}
