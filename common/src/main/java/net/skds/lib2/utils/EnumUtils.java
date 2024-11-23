package net.skds.lib2.utils;

@SuppressWarnings("unused")
public interface EnumUtils<T extends Enum<T>> {

	private Enum<?> asEnum() {
		return (Enum<?>) this;
	}

	default boolean lEqual(T other) {
		return asEnum().ordinal() <= other.ordinal();
	}

	default boolean gEqual(T other) {
		return asEnum().ordinal() >= other.ordinal();
	}
}
