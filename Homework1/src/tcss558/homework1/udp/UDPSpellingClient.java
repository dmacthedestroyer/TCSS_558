package tcss558.homework1.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tcss558.homework1.Log;

public class UDPSpellingClient {
	public static void main(String[] args) {
		if (args.length < 3) {
			printHelp();
			return;
		}

		InetAddress host;

		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			Log.err(String.format("Host '%s' is unknown", e.getMessage()));
			return;
		}

		InetSocketAddress socketAddress;
		try {
			socketAddress = new InetSocketAddress(host, Integer.parseInt(args[1]));
		} catch (NumberFormatException nfe) {
			Log.err("Port number must be an integer");
			return;
		} catch (IllegalArgumentException iae) {
			Log.err(String.format("Port number '%s' is not valid", args[1]));
			return;
		}

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		for (int i = 2; i < args.length; i++) {
			Log.out(String.format("sending word '%s'", args[i]));
			sendData = args[i].getBytes();
			DatagramPacket sendPacket;
			try {
				sendPacket = new DatagramPacket(sendData, sendData.length, socketAddress);
			} catch (SocketException e1) {
				e1.printStackTrace();
				return;
			}
			try {
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
		}
		clientSocket.close();
	}

	private static void printHelp() {
		System.out.println("about text");
	}
}
