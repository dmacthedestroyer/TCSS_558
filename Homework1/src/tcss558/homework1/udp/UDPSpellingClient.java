package tcss558.homework1.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import tcss558.homework1.Log;

public class UDPSpellingClient {
	public static void main(String[] args) {
		try (DatagramSocket clientSocket = new DatagramSocket()) {
			clientSocket.setSoTimeout(1000);

			InetAddress address = InetAddress.getByName(args[0]);
			int port = Integer.parseInt(args[1]);
			for (int i = 2; i < args.length; i++) {
				String output = args[i];
				byte[] sendData = output.getBytes();
				clientSocket.send(new DatagramPacket(sendData, sendData.length, address, port));

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
