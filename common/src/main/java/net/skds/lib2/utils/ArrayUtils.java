package net.skds.lib2.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
public class ArrayUtils {

	public static final byte[] EMPTY_BYTE = {};
	public static final boolean[] EMPTY_BOOLEAN = {};
	public static final char[] EMPTY_CHAR = {};
	public static final short[] EMPTY_SHORT = {};
	public static final int[] EMPTY_INT = {};
	public static final float[] EMPTY_FLOAT = {};
	public static final long[] EMPTY_LONG = {};
	public static final double[] EMPTY_DOUBLE = {};
	public static final Object[] EMPTY_OBJECT = {};

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<T> collection, Class<T> type) {
		return collection.toArray((T[]) Array.newInstance(type, collection.size()));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}

	public static void copySameSize(Object from, Object to, int size) {
		System.arraycopy(from, 0, to, 0, size);
	}

	public static <T> int find(T o, T[] array) {
		for (int i = 0; i < array.length; i++) {
			if (o.equals(array[i])) {
				return i;
			}
		}
		return -1;
	}

	public static int loop(int pos, int length) {
		int ret = pos % length;
		if (ret < 0) ret += length;
		return ret;
	}

	public static byte loop(byte[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static boolean loop(boolean[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static short loop(short[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static char loop(char[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static int loop(int[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static long loop(long[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static float loop(float[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static double loop(double[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static <T> T loop(T[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static class ObjHashedArray {

		public final Object[] array;
		private final int hash;

		public ObjHashedArray(Object[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ObjHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class IntHashedArray {

		public final int[] array;
		private final int hash;

		public IntHashedArray(int[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof IntHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class ByteHashedArray {

		public final byte[] array;
		private final int hash;

		public ByteHashedArray(byte[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ByteHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class CharHashedArray {

		public final char[] array;
		private final int hash;

		public CharHashedArray(char[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof CharHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class ShortHashedArray {

		public final short[] array;
		private final int hash;

		public ShortHashedArray(short[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ShortHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class FloatHashedArray {

		public final float[] array;
		private final int hash;

		public FloatHashedArray(float[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof FloatHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class DoubleHashedArray {

		public final double[] array;
		private final int hash;

		public DoubleHashedArray(double[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof DoubleHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class LongHashedArray {

		public final long[] array;
		private final int hash;

		public LongHashedArray(long[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof LongHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class BooleanHashedArray {

		public final boolean[] array;
		private final int hash;

		public BooleanHashedArray(boolean[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof BooleanHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}
}
