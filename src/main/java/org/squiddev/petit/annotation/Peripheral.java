package org.squiddev.petit.annotation;

import dan200.computercraft.api.peripheral.IPeripheral;

import java.lang.annotation.*;

/**
 * Represents a peripheral to be wrapped.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Inherited
public @interface Peripheral {
	/**
	 * The name of the peripheral
	 *
	 * @return The name of the peripheral.
	 * @see IPeripheral#getType()
	 */
	String value();
}
