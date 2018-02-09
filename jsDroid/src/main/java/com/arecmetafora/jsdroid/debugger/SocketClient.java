package com.arecmetafora.jsdroid.debugger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * TCP client to establish connection to a server and send or receive commands through it.
 */
class SocketClient implements Runnable {

	/**
	 * Connection timeout of 5s.
	 */
	private static final int CONNECTION_TIMEOUT = 3000;
	/**
	 * Socket to receive incoming messages and send outgoing messages to server.
	 */
	private Socket socket;
	/**
	 * Input stream to receive messages from server.
	 */
	private BufferedReader input;
	/**
	 * Output stream to send messages to server.
	 */
	private BufferedWriter output;
	/**
	 * Listener of socket actions.
	 */
	private Listener listener;
	/**
	 * The server host name.
	 */
	private String host;
	/**
	 * The server port.
	 */
	private int port;

	/**
	 * Creates a new socket client.
	 *
	 * @param host The server host name.
	 * @param port The server port.
	 * @param listener Listener for socket actions.
	 */
	SocketClient(String host, int port, Listener listener) {
		this.host = host;
		this.port = port;
		this.listener = listener;
	}

	/**
	 * Connects the client to server.
	 */
	void connect() throws IOException {
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(this.host, this.port), CONNECTION_TIMEOUT);
		this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.output = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

		if (this.listener != null) {
			this.listener.onConnectionEstablished();
		}

		new Thread(this).start();
	}

	/**
	 * Closes the service channel.
	 */
	void disconnect() {
		try {
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException ignored) {
		}

		if (this.listener != null) {
			this.listener.onConnectionLost();
		}
	}

	/**
	 * @return The server host name.
	 */
	String getHost() {
		return this.host;
	}

	/**
	 * @return The server port.
	 */
	int getPort() {
		return this.port;
	}

	/**
	 * @return Whether the socket is connected.
	 */
	boolean isConnected() {
		return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
	}

	@Override
	public void run() {

		try {
			// Keep listening until an socket is closed
			while (isConnected()) {

				String message = this.input.readLine();

				if (message == null) {
					disconnect();
				} else {
					if (this.listener != null) {
						this.listener.onMessageReceived(message);
					}
				}
			}

		} catch (IOException e) {
			disconnect();
		}
	}

	/**
	 * Sends a message to the server.
	 *
	 * @param message The message to be sent.
	 */
	void sendMessage(String message) throws IOException {
		this.output.write(message + "\n");
		this.output.flush();
	}

	/**
	 * Listener for connection changes and message receiving.
	 */
	interface Listener {

		/**
		 * Event fired when the connection is established.
		 */
		void onConnectionEstablished();

		/**
		 * Event fired when the connection is lost.
		 */
		void onConnectionLost();

		/**
		 * Event fired when a message is received.
		 *
		 * @param message The message that was sent by server.
		 */
		void onMessageReceived(String message);
	}
}
