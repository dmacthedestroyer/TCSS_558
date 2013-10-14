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
	public static void main(String[] args) {
		if (args.length != 2) {
			Log.err("todo: produce help text");
			return;
		}

		InetSocketAddress socketAddress;

		try {
			int port = Integer.parseInt(args[0]);
			socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
		} catch (NumberFormatException nfe) {
			Log.err("Port number must be an integer");
			return;
		} catch (UnknownHostException uhe) {
			Log.err(uhe.getMessage());
			return;
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			return;
		}

		WordList wordList;
		try {
			wordList = new WordList(args[1]);
		} catch (IOException e) {
			Log.err(e.getMessage());
			return;
		}

		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(socketAddress);

			Log.out(String.format("connected to %s", socketAddress));

			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				String sentence = new String(receivePacket.getData());
				Log.out("RECEIVED: " + sentence);

				byte[] sendData = sentence.toUpperCase().getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
				serverSocket.send(sendPacket);
			}
		} catch (SocketException e) {
			Log.err(e.getMessage());
			return;
		} catch (IOException ioe) {
			Log.err(ioe.getMessage());
			return;
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}
}
