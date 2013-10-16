package tcss558.homework1.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.Arrays;

import tcss558.homework1.Log;
import tcss558.homework1.SpellingClient;
import tcss558.homework1.SpellingClientArgumentException;
import tcss558.homework1.SpellingInputStream;

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

				Charset charset = Charset.forName("US-ASCII");

				ByteArrayOutputStream sendData = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(sendData);
				out.write(output.getBytes(charset));
				out.writeByte(0);
				clientSocket.send(new DatagramPacket(sendData.toByteArray(), sendData.size(), client.getAddress(), client.getPort()));

				byte[] buf = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(receivePacket);

				try (SpellingInputStream reader = new SpellingInputStream(new ByteArrayInputStream(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength())))) {
					String input = reader.readNullTerminatedString();
					if (!output.equals(input))
						Log.err(String.format("Received malformed packet of size %s from %s", buf.length, clientSocket.getRemoteSocketAddress()));
					else {
						int suggestionCount = reader.read();
						if (suggestionCount < 0)
							Log.err(String.format("Received malformed packet of size %s from %s", buf.length, clientSocket.getRemoteSocketAddress()));
						else if (suggestionCount == 0) {
							int zeroIfSpelledCorrectly = reader.read();
							if (zeroIfSpelledCorrectly == 0) {
								Log.out(String.format("  %s is spelled correctly.", output));
							} else if (zeroIfSpelledCorrectly == -1) {
								Log.out(String.format("  %s is spelled incorrectly.  There are no suggested words.", output));
							} else {
								Log.err(String.format("Received malformed packet of size %s from %s", buf.length, clientSocket.getRemoteSocketAddress()));
							}
						} else {
							String logMessage = String.format("  %s is spelled incorrectly.  There are %s suggested words: ", output, suggestionCount);
							String suggestion;
							while ((suggestion = reader.readNullTerminatedString()) != null)
								logMessage += " " + suggestion;

							Log.out(logMessage);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
