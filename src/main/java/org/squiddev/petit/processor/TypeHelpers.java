package org.squiddev.petit.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import java.lang.reflect.Array;

/**
 * Helpers for type
 */
public final class TypeHelpers {
	private final ProcessingEnvironment environment;

	public TypeHelpers(ProcessingEnvironment environment) {
		this.environment = environment;
	}

	public Class<?> getType(TypeMirror mirror) throws ClassNotFoundException {
		switch (mirror.getKind()) {
			case BOOLEAN:
				return boolean.class;
			case BYTE:
				return byte.class;
			case SHORT:
				return short.class;
			case INT:
				return int.class;
			case LONG:
				return long.class;
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

	public boolean isObjectArray(TypeMirror mirror) {
		if (mirror.getKind() == TypeKind.ARRAY) {
			TypeMirror base = ((ArrayType) mirror).getComponentType();
			return base.getKind() == TypeKind.DECLARED && ((TypeElement) ((DeclaredType) base).asElement()).getQualifiedName().toString().equals("java.lang.Object");
		}

		return false;
	}

	public TypeMirror boxType(TypeMirror mirror) {
		switch (mirror.getKind()) {
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
				return environment.getTypeUtils().boxedClass((PrimitiveType) mirror).asType();
			case ARRAY:
				return environment.getTypeUtils().getArrayType(boxType(((ArrayType) mirror).getComponentType()));
			default:
				return mirror;
		}
	}

	private TypeKind getKind(Class<?> type) {
		if (type == boolean.class) {
			return TypeKind.BOOLEAN;
		} else if (type == byte.class) {
			return TypeKind.BYTE;
		} else if (type == short.class) {
			return TypeKind.SHORT;
		} else if (type == int.class) {
			return TypeKind.INT;
		} else if (type == long.class) {
			return TypeKind.LONG;
		} else if (type == char.class) {
			return TypeKind.CHAR;
		} else if (type == float.class) {
			return TypeKind.FLOAT;
		} else if (type == double.class) {
			return TypeKind.DOUBLE;
		} else if (type == void.class) {
			return TypeKind.VOID;
		}

		return null;
	}

	public TypeMirror getMirror(Class<?> type) {
		if (type.isPrimitive()) {
			return environment.getTypeUtils().getPrimitiveType(getKind(type));
		} else if (type.isArray()) {
			return environment.getTypeUtils().getArrayType(getMirror(type.getComponentType()));
		}

		return environment.getElementUtils().getTypeElement(type.getCanonicalName()).asType();
	}
}
