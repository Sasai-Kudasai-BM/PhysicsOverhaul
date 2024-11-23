package net.skds.lib2.network.demo;

import lombok.AllArgsConstructor;
import net.skds.lib2.network.InPacket;
import net.skds.lib2.network.OutPacket;
import net.skds.lib2.utils.SKDSByteBuf;

@AllArgsConstructor
public class DemoPacket implements OutPacket, InPacket<TCPTest.ServerConnection> {

	private final String text;

	public DemoPacket(SKDSByteBuf buf) {
		text = buf.readSizedString();
	}

	@Override
	public void handle(TCPTest.ServerConnection connection) {
	}

	@Override
	public boolean instantHandle(TCPTest.ServerConnection connection) {
		System.out.println(text);
		return true;
	}

	@Override
	public void writePacket(SKDSByteBuf buffer) {
		buffer.writeSizedString(text);
	}

	@Override
	public int getPacketId() {
		return 1;

	}
}
