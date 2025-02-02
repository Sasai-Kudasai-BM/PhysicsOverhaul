package net.skds.lib2.utils.collection;

import java.util.BitSet;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

// Copy of jdk.jfr.internal.LongMap
@SuppressWarnings("unused")
public class LongMap<T> {
	private static final int MAXIMUM_CAPACITY = 1 << 30;
	private static final long[] EMPTY_KEYS = new long[0];
	private static final Object[] EMPTY_OBJECTS = new Object[0];
	private static final int DEFAULT_SIZE = 32;
	private static final Object NULL_OBJECT = new Object();

	private final int bitCount;
	private BitSet bitSet;
	private long[] keys = EMPTY_KEYS;

	@SuppressWarnings("unchecked")
	private T[] objects = (T[]) EMPTY_OBJECTS;
	private int count;
	private int shift;

	public LongMap() {
		this.bitCount = 0;
	}

	public LongMap(int markBits) {
		this.bitCount = markBits;
		this.bitSet = new BitSet();
	}


	@SuppressWarnings("unchecked")
	// Should be 2^n
	private void initialize(int capacity) {
		keys = new long[capacity];
		objects = (T[]) new Object[capacity];
		shift = 64 - (31 - Integer.numberOfLeadingZeros(capacity));
	}

	public void claimBits() {
		// flip last bit back and forth to make bitset expand to max size
		int lastBit = bitSetIndex(objects.length - 1, bitCount - 1);
		bitSet.flip(lastBit);
		bitSet.flip(lastBit);
	}

	public void setId(long id, int bitIndex) {
		int bitSetIndex = bitSetIndex(tableIndexOf(id), bitIndex);
		bitSet.set(bitSetIndex, true);
	}

	public void clearId(long id, int bitIndex) {
		int bitSetIndex = bitSetIndex(tableIndexOf(id), bitIndex);
		bitSet.set(bitSetIndex, false);
	}

	public void clearId(long id) {
		int bitSetIndex = bitSetIndex(tableIndexOf(id), 0);
		for (int i = 0; i < bitCount; i++) {
			bitSet.set(bitSetIndex + i, false);
		}
	}

	public boolean isSetId(long id, int bitIndex) {
		int bitSetIndex = bitSetIndex(tableIndexOf(id), bitIndex);
		return bitSet.get(bitSetIndex);
	}

	private int bitSetIndex(int tableIndex, int bitIndex) {
		return bitCount * tableIndex + bitIndex;
	}

	private int tableIndexOf(long id) {
		int index = index(id);
		while (true) {
			if (objects[index] == null) {
				throw new InternalError("Unknown id");
			}
			if (keys[index] == id) {
				return index;
			}
			index++;
			if (index == keys.length) {
				index = 0;
			}
		}
	}

	public boolean hasKey(long id) {
		if (keys == EMPTY_KEYS) {
			return false;
		}
		int index = index(id);
		while (true) {
			if (objects[index] == null) {
				return false;
			}
			if (keys[index] == id) {
				return true;
			}
			index++;
			if (index == keys.length) {
				index = 0;
			}
		}
	}

	public void expand(int size) {
		int l = 4 * size / 3;
		if (l <= keys.length) {
			return;
		}
		int n = tableSizeFor(l);
		LongMap<T> temp = new LongMap<>(bitCount);
		temp.initialize(n);
		// Optimization, avoid growing while copying bits
		if (bitCount > 0 && !bitSet.isEmpty()) {
			temp.claimBits();
			claimBits();
		}
		for (int tIndex = 0; tIndex < keys.length; tIndex++) {
			T o = objects[tIndex];
			if (o != null) {
				long key = keys[tIndex];
				temp.put(key, o);
				if (bitCount != 0) {
					for (int bIndex = 0; bIndex < bitCount; bIndex++) {
						boolean bitValue = isSetId(key, bIndex);
						if (bitValue) {
							temp.setId(key, bIndex);
						}
					}
				}
			}
		}
		keys = temp.keys;
		objects = temp.objects;
		shift = temp.shift;
		bitSet = temp.bitSet;
	}

	@SuppressWarnings("unchecked")
	public void put(long id, T object) {
		if (keys == EMPTY_KEYS) {
			// Lazy initialization
			initialize(DEFAULT_SIZE);
		}
		if (object == null) {
			object = (T) NULL_OBJECT;
		}

		int index = index(id);
		// probe for empty slot
		while (true) {
			if (objects[index] == null) {
				keys[index] = id;
				objects[index] = object;
				count++;
				// Don't expand lazy since it
				// can cause resize when replacing
				// an object.
				if (count > 3 * keys.length / 4) {
					expand(2 * keys.length);
				}
				return;
			}
			// if it already exists, replace
			if (keys[index] == id) {
				objects[index] = object;
				return;
			}
			index++;
			if (index == keys.length) {
				index = 0;
			}
		}
	}

	public T getAt(int tableIndex) {
		T o = objects[tableIndex];
		return o == NULL_OBJECT ? null : o;
	}

	public T get(long id) {
		if (keys == EMPTY_KEYS) {
			return null;
		}
		int index = index(id);
		while (true) {
			if (objects[index] == null) {
				return null;
			}
			if (keys[index] == id) {
				return getAt(index);
			}
			index++;
			if (index == keys.length) {
				index = 0;
			}
		}
	}

	private int index(long id) {
		return (int) ((id * -7046029254386353131L) >>> shift);
	}

	// Copied from HashMap::tableSizeFor
	private static int tableSizeFor(int cap) {
		int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	public void forEachKey(LongConsumer keyTraverser) {
		for (int i = 0; i < keys.length; i++) {
			if (objects[i] != null) {
				keyTraverser.accept(keys[i]);
			}
		}
	}

	public void forEach(Consumer<T> consumer) {
		for (int i = 0; i < keys.length; i++) {
			T o = objects[i];
			if (o != null) {
				consumer.accept(o);
			}
		}
	}

	public int size() {
		return count;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			sb.append(i);
			sb.append(": id=");
			sb.append(keys[i]);
			sb.append(" ");
			sb.append(objects[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
}
