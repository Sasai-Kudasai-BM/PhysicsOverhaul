package net.skds.lib2.network;

import net.skds.lib2.utils.SKDSByteBuf;

public interface OutPacket {
	void writePacket(SKDSByteBuf buffer);

	int getPacketId();

	default void send(AbstractConnection<?> connection) {
		connection.sendPacket(this);
	}
}
