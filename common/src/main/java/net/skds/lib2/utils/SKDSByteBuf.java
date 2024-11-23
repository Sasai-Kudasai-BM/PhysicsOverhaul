package net.skds.lib2.utils;

import lombok.Getter;
import net.skds.lib2.mat.Quat;
import net.skds.lib2.mat.VarInt;
import net.skds.lib2.mat.Vec3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class SKDSByteBuf {

	@Getter
	private final ByteBuffer buffer;
	//public final int capacity;
	//private int readerOffset, writerOffset;

	public SKDSByteBuf(ByteBuffer buffer) {
		this.buffer = buffer;
		// this.capacity = buffer.capacity();
	}

	public SKDSByteBuf(byte[] array) {
		this.buffer = ByteBuffer.wrap(array);
		// this.capacity = buffer.capacity();
	}

	public static SKDSByteBuf allocate(int size) {
		return new SKDSByteBuf(ByteBuffer.allocate(size));
	}

	public void clear() {
		buffer.clear();
	}

	public String remainingAsString() {
		return new String(buffer.array(), buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
	}

	public void writeSizedString(String string) {
		final var bytes = string.getBytes(StandardCharsets.UTF_8);
		writeVarInt(bytes.length);
		buffer.put(bytes);
	}

	public String readSizedString(int maxLength) {
		final int length = readVarInt();
		byte[] bytes = new byte[length];
		try {
			this.buffer.get(bytes);
		} catch (BufferUnderflowException e) {
			throw new RuntimeException("Could not read " + length + ", " + buffer.remaining() + " remaining.");
		}
		final String str = new String(bytes, StandardCharsets.UTF_8);
		return str;
	}

	public <T> void writeOptional(Optional<T> op, BiConsumer<SKDSByteBuf, T> consumer) {
		if (op.isPresent()) {
			this.writeBoolean(true);
			consumer.accept(this, op.get());
		} else {
			this.writeBoolean(false);
		}
	}

	public <T> void writeOptional(T op, BiConsumer<SKDSByteBuf, T> consumer) {
		if (op != null) {
			this.writeBoolean(true);
			consumer.accept(this, op);
		} else {
			this.writeBoolean(false);
		}
	}

	public void writeFloatQuat(Quat q) {
		buffer.putFloat((float) q.x());
		buffer.putFloat((float) q.y());
		buffer.putFloat((float) q.z());
		buffer.putFloat((float) q.w());
	}

	public void writeDoubleVector(Vec3 vec) {
		buffer.putDouble(vec.x());
		buffer.putDouble(vec.y());
		buffer.putDouble(vec.z());
	}

	public void writeFloatVector(Vec3 vec) {
		buffer.putFloat((float) vec.x());
		buffer.putFloat((float) vec.y());
		buffer.putFloat((float) vec.z());
	}

	//public void writeNBT(String name, NBT tag) {
	//    if (nbtWriter == null) {
	//        this.nbtWriter = new NBTWriter(this, CompressedProcesser.NONE);
	//    }
	//    try {
	//        nbtWriter.writeNamed(name, tag);
	//    } catch (IOException e) {
	//        // should not throw, as nbtWriter points to this PacketWriter
	//        MinecraftServer.getExceptionManager().handleException(e);
	//    }
	//}

	public void writeBitSet(BitSet bitSet) {
		if (bitSet == null || bitSet.isEmpty()) {
			writeVarInt(0);
		} else {
			long[] array = bitSet.toLongArray();
			writeVarInt(array.length);
			for (int i = 0; i < array.length; i++) {
				putLong(array[i]);
			}
		}
	}

	public void writeBoolean(boolean b) {
		buffer.put((byte) (b ? 1 : 0));
	}

	public boolean readBoolean() {
		return buffer.get() == 1;
	}

	public String readString(int length) {
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public byte[] getBytes(int length) {
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	public void getBytes(byte[] bytes) {
		buffer.get(bytes);
	}

	public UUID readUUID() {
		return new UUID(buffer.getLong(), buffer.getLong());
	}

	public SKDSByteBuf remainingAsCopy() {
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return new SKDSByteBuf(ByteBuffer.wrap(data));
	}

	public byte[] remainingAsArray() {
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return data;
	}

	public void writeUUID(UUID uuid) {
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
	}

	public void writeByteArray(byte[] array) {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		buffer.put(array);
	}

	public byte[] readByteArray() {
		int len = readVarInt();
		if (len == 0) {
			return ArrayUtils.EMPTY_BYTE;
		}
		byte[] arr = new byte[len];
		buffer.get(arr);
		return arr;
	}

	public int readUnsignedShort() {
		return buffer.getShort() & 0xFFFF;
	}

	public String readSizedString() {
		return readSizedString(Integer.MAX_VALUE);
	}

	public byte[] array() {
		return buffer.array();
	}

	public boolean canRead(int bytes) {
		return buffer.remaining() >= bytes;
	}

	public void rewind() {
		buffer.rewind();
	}

	public SKDSByteBuf flip() {
		buffer.flip();
		return this;
	}

	public void skip(int bytes) {
		buffer.position(buffer.position() + bytes);
	}

	public void position(int newPos) {
		buffer.position(newPos);
	}

	public void mark() {
		buffer.mark();
	}

	public void reset() {
		buffer.reset();
	}

	public int capacity() {
		return buffer.capacity();
	}

	public int remaining() {
		return buffer.remaining();
	}

	public boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	public int position() {
		return buffer.position();
	}

	public byte get() {
		return buffer.get();
	}

	public byte get(int index) {
		return buffer.get(index);
	}

	public ByteBuffer slice() {
		return buffer.slice();
	}

	public SKDSByteBuf slice(int index, int length) {
		return new SKDSByteBuf(buffer.slice(index, length));
	}

	public ByteBuffer duplicate() {
		return buffer.duplicate();
	}

	public ByteBuffer asReadOnlyBuffer() {
		return buffer.asReadOnlyBuffer();
	}

	public ByteBuffer write(ByteBuffer b) {
		return buffer.put(b);
	}

	public ByteBuffer put(byte[] b) {
		return buffer.put(b);
	}

	public ByteBuffer put(byte b) {
		return buffer.put(b);
	}

	public ByteBuffer put(int index, byte b) {
		return buffer.put(index, b);
	}

	public ByteBuffer compact() {
		return buffer.compact();
	}

	public boolean isDirect() {
		return buffer.isDirect();
	}

	public char getChar() {
		return buffer.getChar();
	}

	public ByteBuffer putChar(char value) {
		return buffer.putChar(value);
	}

	public char getChar(int index) {
		return buffer.getChar(index);
	}

	public ByteBuffer putChar(int index, char value) {
		return buffer.putChar(index, value);
	}

	public CharBuffer asCharBuffer() {
		return buffer.asCharBuffer();
	}

	public short getShort() {
		return buffer.getShort();
	}

	public ByteBuffer putShort(short value) {
		return buffer.putShort(value);
	}

	public short getShort(int index) {
		return buffer.getShort(index);
	}

	public ByteBuffer putShort(int index, short value) {
		return buffer.putShort(index, value);
	}

	public ShortBuffer asShortBuffer() {
		return buffer.asShortBuffer();
	}

	public int getInt() {
		return buffer.getInt();
	}

	public ByteBuffer putInt(int value) {
		return buffer.putInt(value);
	}

	public int getInt(int index) {
		return buffer.getInt(index);
	}

	public ByteBuffer putInt(int index, int value) {
		return buffer.putInt(index, value);
	}

	public IntBuffer asIntBuffer() {
		return buffer.asIntBuffer();
	}

	public long getLong() {
		return buffer.getLong();
	}

	public ByteBuffer putLong(long value) {
		return buffer.putLong(value);
	}

	public long getLong(int index) {
		return buffer.getLong(index);
	}

	public ByteBuffer putLong(int index, long value) {
		return buffer.putLong(index, value);
	}

	public LongBuffer asLongBuffer() {
		return buffer.asLongBuffer();
	}

	public float getFloat() {
		return buffer.getFloat();
	}

	public ByteBuffer putFloat(float value) {
		return buffer.putFloat(value);
	}

	public float getFloat(int index) {
		return buffer.getFloat(index);
	}

	public ByteBuffer putFloat(int index, float value) {
		return buffer.putFloat(index, value);
	}

	public FloatBuffer asFloatBuffer() {
		return buffer.asFloatBuffer();
	}

	public double getDouble() {
		return buffer.getDouble();
	}

	public ByteBuffer putDouble(double value) {
		return buffer.putDouble(value);
	}

	public double getDouble(int index) {
		return buffer.getDouble(index);
	}

	public ByteBuffer putDouble(int index, double value) {
		return buffer.putDouble(index, value);
	}

	public DoubleBuffer asDoubleBuffer() {
		return buffer.asDoubleBuffer();
	}

	public boolean isReadOnly() {
		return buffer.isReadOnly();
	}

	public static int getVarIntSize(int input) {
		return (input & 0xFFFFFF80) == 0
				? 1
				: (input & 0xFFFFC000) == 0
				? 2
				: (input & 0xFFE00000) == 0
				? 3
				: (input & 0xF0000000) == 0
				? 4
				: 5;
	}

	public void writeVarInt(int value) {
		VarInt.writeToBuffer(buffer, value);
	}

	public void writeVarIntHeader(int startIndex, int value) {
		writeVarIntHeader(buffer, startIndex, value);
	}

	public static void writeVarIntHeader(ByteBuffer buffer, int startIndex, int value) {
		buffer.put(startIndex, (byte) (value & 0x7F | 0x80));
		buffer.put(startIndex + 1, (byte) ((value >>> 7) & 0x7F | 0x80));
		buffer.put(startIndex + 2, (byte) (value >>> 14));
	}

	//private static final byte[] empty3 = { 0, 0, 0 };

	public int writeEmptyVarIntHeader() {
		return writeEmptyVarIntHeader(buffer);
	}

	public static int writeEmptyVarIntHeader(ByteBuffer buffer) {
		final int index = buffer.position();
		buffer.position(index + 3);
		return index;
	}

	public int readVarInt() {
		return readVarInt(buffer);
	}

	public static int readVarInt(ByteBuffer buf) {
		// https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L393
		int result = 0;
		for (int shift = 0; ; shift += 7) {
			byte b = buf.get();
			result |= (b & 0x7f) << shift;
			if (b >= 0) {
				return result;
			}
		}
	}

	public long readVarLong() {
		return readVarLong(buffer);
	}

	public static long readVarLong(ByteBuffer buf) {
		// https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L404
		long result = 0;
		for (int shift = 0; shift < 56; shift += 7) {
			byte b = buf.get();
			result |= (b & 0x7fL) << shift;
			if (b >= 0) {
				return result;
			}
		}
		return result | (buf.get() & 0xffL) << 56;
	}

	public void writeVarLong(long value) {
		writeVarLong(buffer, value);
	}

	public static void writeVarLong(ByteBuffer buffer, long value) {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= (byte) 0b10000000;
			}
			buffer.put(temp);
		} while (value != 0);
	}

	public void writeByte(int i) {
		buffer.put((byte) i);
	}

	public <T> void writeCollection(Collection<T> collection, BiConsumer<SKDSByteBuf, T> consumer) {
		writeVarInt(collection.size());
		for (T object : collection) {
			consumer.accept(this, object);
		}
	}

	public <T> List<T> readCollection(Function<SKDSByteBuf, T> reader) {
		final int size = readVarInt();
		final ArrayList<T> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(i, reader.apply(this));
		}
		return list;
	}

	public <T> T[] readArray(Function<SKDSByteBuf, T> reader, Class<T> type) {
		final int size = readVarInt();
		final T[] list = ArrayUtils.createGenericArray(type, size);
		for (int i = 0; i < size; i++) {
			list[i] = reader.apply(this);
		}
		return list;
	}

	public <T> void writeCollection(T[] collection, BiConsumer<SKDSByteBuf, T> consumer) {
		writeVarInt(collection.length);
		for (T object : collection) {
			consumer.accept(this, object);
		}
	}

	public <T extends Enum<T>> T readVarIntEnum(Class<T> type) {
		int index = readVarInt();
		return type.getEnumConstants()[index];
	}

	public <T extends Enum<T>> T readByteEnum(Class<T> type) {
		return type.getEnumConstants()[buffer.get()];
	}

	public void writeVarIntEnum(Enum<?> value) {
		writeVarInt(value.ordinal());
	}

	public OutputStream getOutputStream() {
		return new BufferOutputStream();
	}

	public InputStream getInputStream() {
		return new BufferInputStream();
	}

	private class BufferOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			buffer.put((byte) b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			buffer.put(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			buffer.put(b, off, len);
		}
	}

	private class BufferInputStream extends InputStream {

		@Override
		public int read() throws IOException {
			if (buffer.remaining() <= 0) {
				return -1;
			}
			return Byte.toUnsignedInt(buffer.get());
		}

		@Override
		public int read(byte[] b) throws IOException {
			int count = Math.min(b.length, buffer.remaining());
			buffer.get(b, 0, count);
			return count;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int count = Math.min(b.length, buffer.remaining());
			count = Math.min(count, len);
			buffer.get(b, off, count);
			return count;
		}

	}

	//public void writeBlockPos(BlockPos pos) {
	//	buffer.putLong(pos.asLong());
	//}
	//public BlockPos readBlockPos() {
	//	return BlockPos.fromLong(buffer.getLong());
	//}

}