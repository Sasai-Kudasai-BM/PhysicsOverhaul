package net.skds.lib2.utils;

@SuppressWarnings("unused")
@FunctionalInterface
public interface FiniteTickable {
	boolean tick();

	default boolean willRemove() {
		return !tick();
	}
}
