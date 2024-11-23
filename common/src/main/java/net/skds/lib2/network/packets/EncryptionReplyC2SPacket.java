package net.skds.lib2.network.packets;

import lombok.AllArgsConstructor;
import net.skds.lib2.network.AbstractClientConnection;
import net.skds.lib2.network.InPacket;
import net.skds.lib2.network.OutPacket;
import net.skds.lib2.utils.SKDSByteBuf;

@AllArgsConstructor
public class EncryptionReplyC2SPacket implements OutPacket, InPacket<AbstractClientConnection<?>> {

	private final byte[] secret;
	private final byte[] token;

	public EncryptionReplyC2SPacket(SKDSByteBuf buf) {
		secret = buf.readByteArray();
		token = buf.readByteArray();
	}

	@Override
	public void handle(AbstractClientConnection<?> connection) {
	}

	@Override
	public boolean instantHandle(AbstractClientConnection<?> connection) {
		connection.finishEncryption(secret, token);
		return true;
	}

	@Override
	public void writePacket(SKDSByteBuf buffer) {
		buffer.writeByteArray(secret);
		buffer.writeByteArray(token);
	}

	@Override
	public int getPacketId() {
		return BasicPackets.ENCRYPTION_REPLY_C2S_PACKET;
	}
}
