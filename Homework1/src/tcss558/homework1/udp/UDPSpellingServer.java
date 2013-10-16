package tcss558.homework1.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import tcss558.homework1.Log;
import tcss558.homework1.SpellingServer;
import tcss558.homework1.SpellingServer.ArgumentException;

public class UDPSpellingServer {
	public static void main(String[] args) {
		SpellingServer spellingServer;
		try {
			spellingServer = new SpellingServer(args);
		} catch (ArgumentException ae) {
			Log.err(ae.getMessage());
			return;
		}

		try (DatagramSocket serverSocket = new DatagramSocket(spellingServer.getPort())) {
			while (true)
				try {
					byte[] receiveData = new byte[256];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);

					String input = new String(receivePacket.getData()).trim();
					String output = input.toUpperCase();

					Log.out(input + " -> " + output);

					byte[] sendData = output.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
					serverSocket.send(sendPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}