package tcss558.homework1.udp;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collection;

import tcss558.homework1.Log;
import tcss558.homework1.SpellingInputStream;
import tcss558.homework1.SpellingOutputStream;
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
			Log.out(String.format("Datagram socket opened on port %s", serverSocket.getLocalPort()));
			Log.out("Initilized network.  Ready for queries.");
			while (true)
				try {
					byte[] buffer = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
					serverSocket.receive(receivePacket);

					Log.out(String.format("Query received from %s", receivePacket.getSocketAddress()));

					String input;
					try (SpellingInputStream reader = new SpellingInputStream(receivePacket)) {
						input = reader.readNullTerminatedString();
					}

					Log.out(String.format("  Query word: %s", input));

					try (SpellingOutputStream writer = new SpellingOutputStream()) {
						writer.writeNullTerminatedString(input);
						if (spellingServer.isInList(input)) {
							writer.writeNullByte();
							writer.writeNullByte();

							Log.out("  Word is spelled correctly.");
						} else {
							String logMessage = "  Word is not spelled correctly,  ";
							Collection<String> closeWords = spellingServer.getCloseWords(input);
							writer.writeByte(closeWords.size());

							if (closeWords.size() > 0) {
								logMessage += String.format("%s suggestions:", closeWords.size());
								for (String word : closeWords) {
									logMessage += " " + word;
									writer.writeNullTerminatedString(word);
								}
								logMessage += ".";
							} else {
								logMessage += "no suggestions.";
							}

							Log.out(logMessage);
						}

						serverSocket.send(new DatagramPacket(writer.toByteArray(), writer.size(), receivePacket.getAddress(), receivePacket.getPort()));
					}

					Log.out("Response sent.");
				} catch (Exception e) {
					e.printStackTrace();
				}

		} catch (BindException be) {
			Log.err(be.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}