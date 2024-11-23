package net.skds.lib2.network;

import net.skds.lib2.network.packets.BasicPackets;
import net.skds.lib2.network.packets.EncryptionReplyC2SPacket;
import net.skds.lib2.network.packets.EncryptionStartS2CPacket;
import net.skds.lib2.utils.SKDSByteBuf;

import java.nio.channels.SocketChannel;

public abstract class AbstractClientConnection<T extends AbstractClientConnection<T>> extends AbstractConnection<T> {

	public AbstractClientConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}

	@Override
	public int getTimeout() {
		return 5000;
	}

	@Override
	public void startEncryption() {
		System.out.println("startEncryption");
		this.encryption = new ConnectionEncryption(true);
		sendPacket(new EncryptionStartS2CPacket(this.encryption.getPublicKey(), this.encryption.getToken()));
	}

	@Override
	public void processEncryption(byte[] publicKey, byte[] token) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void finishEncryption(byte[] secret, byte[] token) {
		System.out.println("finishEncryption");
		if (!this.encryption.applySecret(secret, token)) {
			disconnect();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected InPacket<T> createPacket(int id, SKDSByteBuf payload) {
		if (id == BasicPackets.ENCRYPTION_REPLY_C2S_PACKET) return (InPacket<T>) new EncryptionReplyC2SPacket(payload);
		return null;
	}
}
