package tcss558.homework1.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import tcss558.homework1.Log;

public class UDPSpellingServer {
	public static void main(String[] args) {
		try (DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]))) {
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