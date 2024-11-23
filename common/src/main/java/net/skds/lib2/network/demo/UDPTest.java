package net.skds.lib2.network.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class UDPTest {

	public static void main(String[] args) throws IOException {

		InetSocketAddress rec = new InetSocketAddress("localhost", 1489);
		InetSocketAddress sen = new InetSocketAddress("localhost", 1488);

		byte[] data = "zuzaaaz Pizdoss1346".getBytes(StandardCharsets.UTF_8);

		DatagramSocket socket = new DatagramSocket(sen);
		DatagramSocket socket2 = new DatagramSocket(rec);

		//socket.connect(new InetSocketAddress("localhost", 1489));

		DatagramChannel channel = DatagramChannel.open();
		System.out.println(channel);
		System.out.println(channel.connect(rec));

		var p = new DatagramPacket(data, data.length, rec);
		socket.send(p);


		System.out.println(socket.isConnected());
		System.out.println(socket.getBroadcast());
		//socket.send(new DatagramPacket(data, data.length));

		System.out.println(socket.getReceiveBufferSize());
		System.out.println(socket.getSendBufferSize());

		byte[] d2 = new byte[data.length];
		socket2.receive(new DatagramPacket(d2, data.length));

		System.out.println(new String(d2, StandardCharsets.UTF_8));

		socket.close();
		socket2.close();
	}
}
