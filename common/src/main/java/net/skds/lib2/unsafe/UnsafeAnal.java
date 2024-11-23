package net.skds.lib2.unsafe;

import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@UtilityClass
@SuppressWarnings("unused")
public final class UnsafeAnal {

	public static final Unsafe UNSAFE = getUnsafe();

	public static final int BYTE_ARRAY_BASE = UNSAFE.arrayBaseOffset(byte[].class);
	public static final int CHAR_ARRAY_BASE = UNSAFE.arrayBaseOffset(char[].class);
	public static final int SHORT_ARRAY_BASE = UNSAFE.arrayBaseOffset(short[].class);
	public static final int INT_ARRAY_BASE = UNSAFE.arrayBaseOffset(int[].class);
	public static final int FLOAT_ARRAY_BASE = UNSAFE.arrayBaseOffset(float[].class);
	public static final int LONG_ARRAY_BASE = UNSAFE.arrayBaseOffset(long[].class);
	public static final int DOUBLE_ARRAY_BASE = UNSAFE.arrayBaseOffset(double[].class);


	public static void setByte(long address, byte value) {
		UNSAFE.putByte(address, value);
	}

	public static void setByte(long address, int index, byte value) {
		UNSAFE.putByte(address + index, value);
	}

	public static void setShort(long address, short value) {
		UNSAFE.putShort(address, value);
	}

	public static void setShort(long address, int index, short value) {
		UNSAFE.putShort(address + index * 2L, value);
	}

	public static void setInt(long address, int value) {
		UNSAFE.putInt(address, value);
	}

	public static void setInt(long address, int index, int value) {
		UNSAFE.putInt(address + index * 4L, value);
	}

	public static void setFloat(long address, float value) {
		UNSAFE.putFloat(address, value);
	}

	public static void setFloat(long address, int index, float value) {
		UNSAFE.putFloat(address + index * 4L, value);
	}

	public static void setLong(long address, long value) {
		UNSAFE.putLong(address, value);
	}

	public static void setLong(long address, int index, long value) {
		UNSAFE.putLong(address + index * 8L, value);
	}

	public static void setDouble(long address, double value) {
		UNSAFE.putDouble(address, value);
	}

	public static void setDouble(long address, int index, double value) {
		UNSAFE.putDouble(address + index * 8L, value);
	}

	public static byte getByte(long address) {
		return UNSAFE.getByte(address);
	}

	public static byte getByte(long address, int index) {
		return UNSAFE.getByte(address + index);
	}

	public static short getShort(long address) {
		return UNSAFE.getShort(address);
	}

	public static short getShort(long address, int index) {
		return UNSAFE.getShort(address + 2L * index);
	}

	public static int getInt(long address) {
		return UNSAFE.getInt(address);
	}

	public static int getInt(long address, int index) {
		return UNSAFE.getInt(address + 4L * index);
	}

	public static float getFloat(long address) {
		return UNSAFE.getFloat(address);
	}

	public static float getFloat(long address, int index) {
		return UNSAFE.getFloat(address + 4L * index);
	}

	public static long getLong(long address) {
		return UNSAFE.getLong(address);
	}

	public static long getLong(long address, int index) {
		return UNSAFE.getLong(address + 8L * index);
	}

	public static double getDouble(long address) {
		return UNSAFE.getDouble(address);
	}

	public static double getDouble(long address, int index) {
		return UNSAFE.getDouble(address + 8L * index);
	}

	public static void transferArray(long src, byte[] dst, int size, int offset) {
		UNSAFE.copyMemory(null, src, dst, offset + BYTE_ARRAY_BASE, size);
	}

	public static void transferArray(long src, Object dst, long size, long offset, int byteSize, int arrayOffset) {
		//if (NATIVE_ORDER || byteSize == 1) {
		UNSAFE.copyMemory(null, src, dst, offset * byteSize + arrayOffset, size * byteSize);
		//} else {
		//	UNSAFE.copySwapMemory(src, 0, dst, offset, size * byteSize, byteSize);
		//}
	}

	public static void transferArray(long src, short[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 2, SHORT_ARRAY_BASE);
	}

	public static void transferArray(long src, char[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 2, CHAR_ARRAY_BASE);
	}

	public static void transferArray(long src, int[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 4, INT_ARRAY_BASE);
	}

	public static void transferArray(long src, float[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 4, FLOAT_ARRAY_BASE);
	}

	public static void transferArray(long src, long[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 8, LONG_ARRAY_BASE);
	}

	public static void transferArray(long src, double[] dst, int size, int offset) {
		transferArray(src, dst, size, offset, 8, DOUBLE_ARRAY_BASE);
	}

	public static void transferArray(byte[] src, long dst, int size, int offset) {
		UNSAFE.copyMemory(src, offset + BYTE_ARRAY_BASE, null, dst, size);
	}

	public static void transferArray(Object src, long dst, long size, long offset, int byteSize, int arrayOffset) {
		//if (NATIVE_ORDER || byteSize == 1) {
		UNSAFE.copyMemory(src, offset * byteSize + arrayOffset, null, dst, size * byteSize);
		//} else {
		//	UNSAFE.copySwapMemory(src, offset * byteSize + arrayOffset, null, dst, size * byteSize, byteSize);
		//}
	}

	public static void transferArray(short[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 2, SHORT_ARRAY_BASE);
	}

	public static void transferArray(char[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 2, CHAR_ARRAY_BASE);
	}

	public static void transferArray(int[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 4, INT_ARRAY_BASE);
	}

	public static void transferArray(float[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 4, FLOAT_ARRAY_BASE);
	}

	public static void transferArray(long[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 8, LONG_ARRAY_BASE);
	}

	public static void transferArray(double[] src, long dst, int size, int offset) {
		transferArray(src, dst, size, offset, 8, DOUBLE_ARRAY_BASE);
	}


	private static Unsafe getUnsafe() {
		try {
			Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			Field f = unsafeClass.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
