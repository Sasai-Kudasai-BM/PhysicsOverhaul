package net.skds.lib2.event;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventBus<T extends Event> {

	private final ConcurrentLinkedQueue<EventListener<T>> listeners = new ConcurrentLinkedQueue<>();

	public void addListener(EventListener<T> listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener<T> listener) {
		listeners.remove(listener);
	}

	public boolean post(T event) {
		listeners.forEach(listener -> {
			listener.onEvent(event);
		});
		return event != null && event.canceled;
	}
}
