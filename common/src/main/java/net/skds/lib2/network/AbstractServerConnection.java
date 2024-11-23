package net.skds.lib2.network;

import net.skds.lib2.network.packets.BasicPackets;
import net.skds.lib2.network.packets.EncryptionReplyC2SPacket;
import net.skds.lib2.network.packets.EncryptionStartS2CPacket;
import net.skds.lib2.utils.SKDSByteBuf;

import java.nio.channels.SocketChannel;

public abstract class AbstractServerConnection<T extends AbstractServerConnection<T>> extends AbstractConnection<T> {

	public AbstractServerConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}

	@Override
	public int getTimeout() {
		return 5000;
	}

	@Override
	public void startEncryption() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void processEncryption(byte[] publicKey, byte[] token) {
		System.out.println("processEncryption");
		this.encryption = new ConnectionEncryption(false);
		byte[] secret = this.encryption.createSecret(publicKey);
		sendPacket(new EncryptionReplyC2SPacket(secret, token));
	}

	@Override
	public void finishEncryption(byte[] secret, byte[] token) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected InPacket<T> createPacket(int id, SKDSByteBuf payload) {
		if (id == BasicPackets.ENCRYPTION_START_S2C_PACKET) return (InPacket<T>) new EncryptionStartS2CPacket(payload);
		return null;
	}
}
