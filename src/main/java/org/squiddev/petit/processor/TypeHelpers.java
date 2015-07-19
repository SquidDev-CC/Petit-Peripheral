package org.squiddev.petit.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Array;

/**
 * Helpers for type
 */
public final class TypeHelpers {
	public static Class<?> getType(TypeMirror mirror) throws ClassNotFoundException {
		switch (mirror.getKind()) {
			case BOOLEAN:
				return boolean.class;
			case BYTE:
				return byte.class;
			case SHORT:
				return short.class;
			case INT:
				return int.class;
			case CHAR:
				return char.class;
			case FLOAT:
				return float.class;
			case DOUBLE:
				return double.class;
			case VOID:
				return void.class;
			case ARRAY:
				return Array.newInstance(getType(((ArrayType) mirror).getComponentType()), 0).getClass();
			case DECLARED:
				String name = ((TypeElement) ((DeclaredType) mirror).asElement()).getQualifiedName().toString();
				try {
					return Class.forName(name);
				} catch (ClassNotFoundException e) {
					throw new ClassNotFoundException("Unknown class " + name);
				}
			default:
				throw new ClassNotFoundException("Unknown type " + mirror.getKind() + " (" + mirror + ")");
		}
	}

	public static Class<?> getType(Element element) throws ClassNotFoundException {
		return getType(element.asType());
	}
}
