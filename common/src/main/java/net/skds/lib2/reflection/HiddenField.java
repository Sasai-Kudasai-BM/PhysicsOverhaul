package net.skds.lib2.reflection;

import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

@AllArgsConstructor
public class HiddenField<T> {
	public final Field field;

	@SuppressWarnings("unchecked")
	public T get(Object o) {
		try {
			return (T) field.get(o);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void set(Object o, T value) {
		try {
			field.set(o, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}

