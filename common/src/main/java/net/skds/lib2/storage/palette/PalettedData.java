package net.skds.lib2.storage.palette;

public class PalettedData {

	public final int dataBits;
	public final int capacity;
	public final int wordMask;
	public final int valuesPerWord;
	public final long[] words;

	public PalettedData(int dataBits, int capacity, long[] words) {
		this.capacity = capacity;
		this.dataBits = dataBits;
		this.valuesPerWord = 64 / dataBits;
		this.words = words;

		this.wordMask = -1 >>> (32 - dataBits);
	}

	public PalettedData(int dataBits, int capacity) {
		this.capacity = capacity;
		this.dataBits = dataBits;
		this.valuesPerWord = 64 / dataBits;
		int longCount = capacity / valuesPerWord + ((capacity % valuesPerWord) > 0 ? 1 : 0);
		this.words = new long[longCount];

		this.wordMask = -1 >>> (32 - dataBits);
	}

	public PalettedData resize(int newBitsize) {
		if (newBitsize == dataBits) {
			return this;
		}
		PalettedData data = new PalettedData(newBitsize, capacity);
		for (int i = 0; i < capacity; i++) {
			data.setValue(i, getValue(i));
		}
		return data;
	}

	public void remap(Remapper remapper) {
		for (int i = 0; i < capacity; i++) {
			int ov = getValue(i);
			int nv = remapper.remap(ov);
			if (nv != ov) {
				setValue(i, nv);
			}
		}
	}

	public void replace(int oldValue, int newValue) {
		for (int i = 0; i < capacity; i++) {
			if (getValue(i) == oldValue) {
				setValue(i, newValue);
			}
		}
	}

	public int getValue(int index) {
		int wordIndex = index / valuesPerWord;
		int wordOffset = ((index % valuesPerWord) * dataBits);

		long result = words[wordIndex] >>> wordOffset;
		return (int) (result & wordMask);
	}

	public int setValue(int index, int value) {
		int wordIndex = index / valuesPerWord;
		int wordOffset = ((index % valuesPerWord) * dataBits);

		long word = words[wordIndex];
		long result = (word >>> wordOffset) & wordMask;

		word &= ~((long) wordMask << wordOffset);
		word |= ((long) (value & wordMask) << wordOffset);

		words[wordIndex] = word;
		return (int) result;
	}

	@FunctionalInterface
	public static interface Remapper {
		public int remap(int value);
	}
}
