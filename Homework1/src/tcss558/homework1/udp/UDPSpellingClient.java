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
			Log.out(String.format("Datagram socket opened on port %s.", clientSocket.getLocalPort()));
			clientSocket.setSoTimeout(1000);
			Log.out("Initialized network.");

			for (String output : client.getWords()) {
				Log.out(String.format("Querying server (%s)", output));

				try (SpellingOutputStream writer = new SpellingOutputStream()) {
					writer.writeNullTerminatedString(output);
					clientSocket.send(new DatagramPacket(writer.toByteArray(), writer.size(), client.getAddress(), client.getPort()));
				}

				byte[] buf = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(receivePacket);

				try (SpellingInputStream reader = new SpellingInputStream(receivePacket)) {
					if (output.equals(reader.readNullTerminatedString())) {
						int suggestionCount = reader.read();
						if (suggestionCount == 0) {
							int zeroIfSpelledCorrectly = reader.read();
							if (zeroIfSpelledCorrectly == 0)
								Log.out(String.format("  %s is spelled correctly.", output));
							else if (zeroIfSpelledCorrectly == -1)
								Log.out(String.format("  %s is spelled incorrectly.  There are no suggested words.", output));
							else
								Log.err(String.format("Received malformed packet of size %s from %s'", receivePacket.getLength(), receivePacket.getSocketAddress()));
						} else if (suggestionCount > 0) {
							String logMessage = String.format("  %s is spelled incorrectly.  There are %s suggested words: ", output, suggestionCount);
							String suggestion;
							while ((suggestion = reader.readNullTerminatedString()) != null)
								logMessage += " " + suggestion;

							Log.out(logMessage);
						}
					} else
						Log.err(String.format("received unsolicited response to query for word %s from %s", output, receivePacket.getSocketAddress()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
