package net.skds.lib2.network;


import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.function.Function;

//TODO
public class TCPServer {

	protected boolean running = false;
	public final Selector selector;
	public final ServerSocketChannel server;
	protected final Object acceptAttachment = new Object();
	@Setter
	private Function<SocketChannel, AbstractClientConnection<?>> connectionFactory;
	private final Executor inputExecutor, outputExecutor;

	public TCPServer(Function<SocketChannel, AbstractClientConnection<?>> connectionFactory, Executor inputExecutor, Executor outputExecutor) {
		this.connectionFactory = connectionFactory;
		this.outputExecutor = outputExecutor;
		this.inputExecutor = inputExecutor;
		try {
			this.selector = Selector.open();
			this.server = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		running = false;
		for (var k : selector.keys()) {
			try {
				k.channel().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start(InetSocketAddress address) {
		running = true;
		try {
			server.bind(address);
			server.configureBlocking(false);
			server.register(selector, SelectionKey.OP_ACCEPT, acceptAttachment);

			final ServerSocket socket = server.socket();
			socket.setSoTimeout(5000);
			inputExecutor.execute(this::inputLoop);
		} catch (IOException e) {
			running = false;
			throw new RuntimeException(e);
		}
	}

	protected void onSelect(SelectionKey key) {
		if (key.isAcceptable()) {
			if (key.attachment() != acceptAttachment) {
				key.cancel();
				return;
			}
			try {
				final SocketChannel sc = server.accept();
				AbstractClientConnection<?> connection = connectionFactory.apply(sc);
				sc.configureBlocking(false);
				final Socket socket = sc.socket();
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(connection.getTimeout());
				outputExecutor.execute(() -> outputLoop(connection));
				sc.register(selector, SelectionKey.OP_READ, connection);
				connection.startEncryption();
			} catch (IOException e) {
				key.cancel();
				e.printStackTrace();
			}
		} else if (key.isReadable() && (key.attachment() instanceof AbstractClientConnection<?> cc)) {
			try {
				cc.read();
			} catch (IOException e) {
				try {
					cc.channel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			key.cancel();
		}
	}

	protected void inputLoop() {
		while (running) {
			try {
				selector.select(this::onSelect);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void outputLoop(AbstractClientConnection<?> connection) {
		while (running && connection.isAlive()) {
			try {
				connection.flushPackets();
			} catch (IOException e) {
				connection.disconnect();
			}
		}
	}
}
