package net.skds.lib2.utils.collection;

import java.util.Arrays;
import java.util.function.LongConsumer;

@SuppressWarnings("unused")
public final class LongList {

	private long[] elements = new long[16];
	private int size = 0;

	private void checkSize() {
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, elements.length * 2);
		}
	}

	public long[] getElements() {
		return elements;
	}

	public long getLong(int pos) {
		return elements[pos];
	}

	public void addLong(long value) {
		checkSize();
		elements[size++] = value;
	}

	public void clear() {
		elements = new long[16];
		size = 0;
	}

	public int size() {
		return size;
	}

	public void forEach(LongConsumer action) {
		for (int i = 0; i < size; i++) {
			action.accept(elements[i]);
		}
	}
}
