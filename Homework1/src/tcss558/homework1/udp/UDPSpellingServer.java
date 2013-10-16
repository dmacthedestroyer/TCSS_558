package tcss558.homework1.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.Collection;

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
			Log.out(String.format("Datagram socket opened on port %s", serverSocket.getLocalPort()));
			Log.out("Initilized network.  Ready for queries.");
			while (true)
				try {
					byte[] buffer = new byte[576];
					DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
					serverSocket.receive(receivePacket);

					Log.out(String.format("Query received from %s", receivePacket.getSocketAddress()));

					byte[] receiveData = receivePacket.getData();
					int nullTerminator;
					for (nullTerminator = 0; nullTerminator < receiveData.length && receiveData[nullTerminator] != 0; nullTerminator++) {
					}

					Charset charset = Charset.forName("US-ASCII");
					String input = new String(receiveData, 0, nullTerminator, charset);

					Log.out(String.format("  Query word: %s", input));

					ByteArrayOutputStream sendData = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(sendData);
					out.write(input.getBytes(charset));
					out.writeByte(0);
					if (spellingServer.isInList(input)) {
						out.writeByte(0);
						out.writeByte(0);

						Log.out("  Word is spelled correctly.");
					} else {
						String logMessage = "  Word is not spelled correctly,  ";
						Collection<String> closeWords = spellingServer.getCloseWords(input, out.size());
						out.writeByte(closeWords.size());

						if (closeWords.size() > 0) {
							logMessage += String.format("%s suggestions:", closeWords.size());
							for (String word : closeWords) {
								logMessage += " " + word;
								out.write(word.getBytes(charset));
								out.writeByte(0);
							}
							logMessage += ".";
						} else {
							logMessage += "no suggestions.";
						}

						Log.out(logMessage);
					}
					out.flush();

					DatagramPacket sendPacket = new DatagramPacket(sendData.toByteArray(), sendData.size(), receivePacket.getAddress(), receivePacket.getPort());
					serverSocket.send(sendPacket);

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