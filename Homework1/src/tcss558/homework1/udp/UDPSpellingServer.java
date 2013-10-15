package tcss558.homework1.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tcss558.homework1.Log;
import tcss558.homework1.WordList;

public class UDPSpellingServer {
	private static InetSocketAddress getSocketAddress(String port) throws IllegalArgumentException {
		InetSocketAddress socketAddress;

		try {
			socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), Integer.parseInt(port));
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Port number must be an integer");
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException(uhe.getMessage());
		} catch (IllegalArgumentException iae) {
			throw new IllegalArgumentException(iae.getMessage());
		}

		return socketAddress;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			Log.err("todo: produce help text");
			return;
		}

		WordList wordList;
		try {
			wordList = new WordList(args[1]);
		} catch (IOException e) {
			Log.err(e.getMessage());
			return;
		}

		try (DatagramSocket serverSocket = new DatagramSocket(getSocketAddress(args[0]))) {
			Log.out(String.format("connected to %s", serverSocket.getLocalSocketAddress()));

			while (true) {
				try {
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);

					String sentence = new String(receivePacket.getData());
					Log.out("RECEIVED: " + sentence);

					byte[] sendData = sentence.toUpperCase().getBytes();

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
					serverSocket.send(sendPacket);
				} catch (IOException ioe) {
					Log.err(ioe.getMessage());
					return;
				}
			}
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			return;
		} catch (SocketException e) {
			Log.err(e.getMessage());
			return;
		}
	}
}
