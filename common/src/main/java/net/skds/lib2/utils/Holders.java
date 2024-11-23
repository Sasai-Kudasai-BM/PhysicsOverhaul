package net.skds.lib2.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("unused")
public final class Holders {
	private Holders() {

	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class ObjectHolder<T> {

		@Getter
		@Setter
		protected T value;

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class IntHolder {

		@Getter
		@Setter
		protected int value;

		public int increment(int inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class LongHolder {

		@Getter
		@Setter
		protected long value;

		public long increment(long inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class FloatHolder {

		@Getter
		@Setter
		protected float value;

		public float increment(float inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class DoubleHolder {

		@Getter
		@Setter
		protected double value;

		public double increment(double inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class BooleanHolder {

		@Getter
		@Setter
		protected boolean value;

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class ByteHolder {

		@Getter
		@Setter
		protected byte value;

		public byte increment(byte inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class CharHolder {

		@Getter
		@Setter
		protected char value;

		public char increment(char inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class ShortHolder {

		@Getter
		@Setter
		protected short value;

		public short increment(short inc) {
			return value += inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}
	}
}
