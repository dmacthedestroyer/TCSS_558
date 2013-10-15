package tcss558.homework1.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tcss558.homework1.Log;

public class UDPSpellingClient {
	private static InetSocketAddress getSocketAddress(String host, String port) throws IllegalArgumentException {
		InetAddress hostAddress;

		try {
			hostAddress = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(String.format("Host '%s' is unknown", e.getMessage()));
		}

		InetSocketAddress socketAddress;
		try {
			socketAddress = new InetSocketAddress(hostAddress, Integer.parseInt(port));
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Port number must be an integer");
		} catch (IllegalArgumentException iae) {
			throw new IllegalArgumentException(String.format("Port number '%s' is not valid", port));
		}

		return socketAddress;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			printHelp();
			return;
		}

		try (DatagramSocket clientSocket = new DatagramSocket()) {
			byte[] receiveData = new byte[1024];

			for (int i = 2; i < args.length; i++) {
				byte[] sendData = args[i].getBytes();
				clientSocket.send(new DatagramPacket(sendData, sendData.length, getSocketAddress(args[0], args[1])));
				
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				System.out.println("FROM SERVER:" + new String(receivePacket.getData()));
			}
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			return;
		} catch (SocketException e) {
			Log.err(e.getMessage());
			return;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}
	}

	private static void printHelp() {
		System.out.println("about text");
	}
}
