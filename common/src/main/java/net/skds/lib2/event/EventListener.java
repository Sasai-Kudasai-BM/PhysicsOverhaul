package net.skds.lib2.event;

@FunctionalInterface
public interface EventListener<T extends Event> {
	void onEvent(T event);
}
