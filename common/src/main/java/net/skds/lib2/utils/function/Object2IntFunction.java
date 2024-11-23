package net.skds.lib2.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2IntFunction<T> {
	int get(T o);
}
