package org.squiddev.petit;

import dan200.computercraft.api.peripheral.IPeripheral;

public class PeripheralHelper {
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends IPeripheral> getClass(Class<T> klass) {
		try {
			return (Class<? extends IPeripheral>) Class.forName(klass.getName().replace('$', '_') + "_Peripheral");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> IPeripheral create(Class<? extends T> klass, T instance) {
		try {
			Class<?> peripheral = getClass(klass);
			return (IPeripheral) peripheral.getConstructor(klass).newInstance(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static IPeripheral create(Object instance) {
		return create(instance.getClass(), instance);
	}
}
