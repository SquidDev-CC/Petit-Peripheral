package org.squiddev.petit;


import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.HashMap;
import java.util.Map;

public class PeripheralWrapper {
	public final IPeripheral peripheral;
	private final Map<String, Integer> nameLookup;

	public PeripheralWrapper(IPeripheral peripheral) {
		this.peripheral = peripheral;

		Map<String, Integer> nameLookup = this.nameLookup = new HashMap<String, Integer>();
		String[] names = peripheral.getMethodNames();
		for (int i = 0; i < names.length; i++) {
			nameLookup.put(names[i], i);
		}
	}

	public Object[] call(String name, Object... args) {
		try {
			return peripheral.callMethod(null, null, nameLookup.get(name), args);
		} catch (LuaException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
