package tcss558.homework1.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import tcss558.homework1.Log;
import tcss558.homework1.SpellingClient;
import tcss558.homework1.SpellingClientArgumentException;

public class UDPSpellingClient {
	public static void main(String[] args) {
		SpellingClient client;
		try {
			client = new SpellingClient(args);
		} catch (SpellingClientArgumentException e) {
			Log.err(e.getMessage());
			return;
		}

		try (DatagramSocket clientSocket = new DatagramSocket()) {
			clientSocket.setSoTimeout(1000);

			for (String output : client.getWords()) {
				byte[] sendData = output.getBytes();
				clientSocket.send(new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort()));

				byte[] receiveData = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				String input = new String(receivePacket.getData());

				Log.out(output + " -> " + input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
