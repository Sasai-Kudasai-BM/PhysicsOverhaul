package net.skds.lib2.mat;

import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class VarInt extends Number implements Comparable<Integer> {

	public final int value;

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Number) {
			return value == ((Number) obj).intValue();
		}
		return false;
	}

	@Override
	public int compareTo(Integer o) {
		return Integer.compare(value, o);
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	public static void writeToBuffer(ByteBuffer buf, int value) {
		if ((value & (0xFFFFFFFF << 7)) == 0) {
			buf.put((byte) value);
		} else if ((value & (0xFFFFFFFF << 14)) == 0) {
			buf.putShort((short) ((value & 0x7F | 0x80) << 8 | (value >>> 7)));
		} else if ((value & (0xFFFFFFFF << 21)) == 0) {
			buf.put((byte) (value & 0x7F | 0x80));
			buf.put((byte) ((value >>> 7) & 0x7F | 0x80));
			buf.put((byte) (value >>> 14));
		} else if ((value & (0xFFFFFFFF << 28)) == 0) {
			buf.putInt((value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
					| ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
		} else {
			buf.putInt((value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
					| ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
			buf.put((byte) (value >>> 28));
		}
	}

}
