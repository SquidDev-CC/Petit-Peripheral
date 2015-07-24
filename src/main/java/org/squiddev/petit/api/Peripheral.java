package org.squiddev.petit.api;

import dan200.computercraft.api.peripheral.IPeripheral;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a peripheral to be wrapped.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Peripheral {
	/**
	 * The name of the peripheral
	 *
	 * @return The name of the peripheral.
	 * @see IPeripheral#getType()
	 */
	String value();
}
