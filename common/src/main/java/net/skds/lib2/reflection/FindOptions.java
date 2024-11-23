package net.skds.lib2.reflection;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public class FindOptions {

	private boolean strictType = true;
	private int ordinal = 0;
	//private String name;
	private Class<?> type;

	public FindOptions(int ordinal, Class<?> type) {
		this.ordinal = ordinal;
		this.type = type;
	}

	public FindOptions(Class<?> type) {
		this.ordinal = -1;
		this.type = type;
	}

	public FindOptions(Class<?> type, boolean strictType) {
		this.ordinal = -1;
		this.type = type;
		this.strictType = strictType;
	}

	public FindOptions(int ordinal, Class<?> type, boolean strictType) {
		this.ordinal = ordinal;
		this.type = type;
		this.strictType = strictType;
	}

	public boolean test(Field field) {
		if (strictType) {
			return field.getType() == type;
		} else {
			return type.isAssignableFrom(field.getType());
		}
	}
}
