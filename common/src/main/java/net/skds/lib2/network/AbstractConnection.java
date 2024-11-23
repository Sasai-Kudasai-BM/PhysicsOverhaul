package net.skds.lib2.network;

import lombok.Setter;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractConnection<T extends AbstractConnection<T>> {


	public final SocketChannel channel;
	protected final int bufferSize;
	protected final float loadFactor = .75f;
	protected final int loadSize;
	protected final ByteBuffer readBuffer;
	protected final SKDSByteBuf readBufferWrapper;
	protected final ByteBuffer writeBuffer;
	protected final SKDSByteBuf writeBufferWrapper;
	protected byte[] remainingIn;
	protected final ConcurrentLinkedQueue<InPacket<T>> inputPackets = new ConcurrentLinkedQueue<>();
	protected final LinkedBlockingQueue<OutPacket> outputPackets = new LinkedBlockingQueue<>();
	@Setter
	protected ConnectionEncryption encryption;

	public AbstractConnection(SocketChannel channel, int bufferSize) {
		this.bufferSize = bufferSize;
		this.channel = channel;
		this.readBuffer = ByteBuffer.allocate(bufferSize);
		this.readBufferWrapper = new SKDSByteBuf(readBuffer);
		this.writeBuffer = ByteBuffer.allocate(bufferSize).flip();
		this.writeBufferWrapper = new SKDSByteBuf(writeBuffer);
		this.loadSize = (int) (bufferSize * loadFactor);
	}

	public void sendPacket(OutPacket packet) {
		//if (!isAlive()) {
		//	return;
		//}
		outputPackets.add(packet);
	}

	public boolean isAlive() {
		return channel.isConnected();
	}

	protected abstract InPacket<T> createPacket(int id, SKDSByteBuf payload);

	public abstract void startEncryption();

	public abstract void processEncryption(byte[] publicKey, byte[] token);

	public abstract void finishEncryption(byte[] secret, byte[] token);

	protected void writePacket(OutPacket packet) {
		try {
			int pos = writeBuffer.position();
			int lim = writeBuffer.limit();
			writeBuffer.limit(writeBuffer.capacity());
			writeBuffer.position(lim);
			writeBuffer.position(lim + 4);
			writeBuffer.putInt(packet.getPacketId());
			packet.writePacket(writeBufferWrapper);
			int pos2 = writeBuffer.position();
			writeBuffer.putInt(lim, pos2 - 4 - lim);
			ConnectionEncryption enc = encryption;
			if (enc != null) {
				enc.encrypt(writeBufferWrapper, lim, pos2);
			}
			writeBuffer.limit(pos2);
			writeBuffer.position(pos);
		} catch (BufferOverflowException e) {
			throw new RuntimeException("packet " + packet.getPacketId() + " is too big", e);
		}
	}

	protected boolean validateWriteBufferSpace() {
		return writeBuffer.capacity() - writeBuffer.limit() > loadSize;
	}

	protected void flushPackets() throws IOException {
		while (validateWriteBufferSpace()) {
			try {
				OutPacket packet = writeBuffer.hasRemaining() ? outputPackets.poll() : outputPackets.take();
				if (packet == null) {
					break;
				}
				writePacket(packet);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		channel.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {
			writeBuffer.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void read() throws IOException {
		readBuffer.clear();
		if (remainingIn != null) {
			readBuffer.put(remainingIn);
			remainingIn = null;
		}
		int readRem = readBuffer.position();
		int bytes = channel.read(readBuffer);
		if (bytes < 0) {
			disconnect();
			return;
		}
		ConnectionEncryption enc = encryption;
		if (enc != null) {
			enc.decrypt(readBufferWrapper, readRem, readBuffer.position());
		}
		readBuffer.flip();
		readBuffer.mark();

		while (readBuffer.remaining() > 0) {
			if (readBuffer.remaining() < 4) {
				markInputRemaining();
				return;
			}
			readBuffer.mark();
			int size = readBuffer.getInt();
			if (size > bufferSize) {
				disconnect();
				return;
			}
			if (size > readBuffer.remaining()) {
				markInputRemaining();
				return;
			}
			ByteBuffer payload = readBuffer.slice(readBuffer.position(), size);
			readBuffer.position(readBuffer.position() + size);
			int id = payload.getInt();
			InPacket<T> packet = createPacket(id, new SKDSByteBuf(payload));
			if (packet != null && !packet.instantHandle((T) this)) {
				inputPackets.offer(packet);
			}
		}
	}

	protected void markInputRemaining() {
		readBuffer.reset();
		remainingIn = new byte[readBuffer.remaining()];
		readBuffer.get(remainingIn);
	}

	public abstract int getTimeout();


	protected void disconnect() {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
