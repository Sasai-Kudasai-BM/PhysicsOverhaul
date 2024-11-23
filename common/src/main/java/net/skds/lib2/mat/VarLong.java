package net.skds.lib2.mat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VarLong extends Number implements Comparable<Long> {

	public final long value;

	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Number) {
			return value == ((Number) obj).longValue();
		}
		return false;
	}

	@Override
	public int compareTo(Long o) {
		return Long.compare(value, o);
	}

	@Override
	public int intValue() {
		return (int) value;
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

}
