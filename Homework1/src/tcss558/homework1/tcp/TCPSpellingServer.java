package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.StringTokenizer;

import tcss558.homework1.Log;
import tcss558.homework1.SpellingServer;
import tcss558.homework1.SpellingServer.ArgumentException;

public class TCPSpellingServer {
	public static void main(String[] args) {
		SpellingServer spellingServer;
		try {
			spellingServer = new SpellingServer(args);
		} catch (ArgumentException ae) {
			Log.err(ae.getMessage());
			return;
		}

		try (ServerSocket serverSocket = new ServerSocket(spellingServer.getPort())) {
			Log.out(String.format("Server socket opened on port %s", serverSocket.getLocalPort()));
			while (true)
				try (Socket connection = serverSocket.accept();
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						PrintWriter out = new PrintWriter(connection.getOutputStream(), true)) {
					Log.out(String.format("Accepted connection from %s", connection.getRemoteSocketAddress()));

					String input;

					while (!connection.isClosed() && (input = "" + in.readLine()).length() > 0) {
						StringTokenizer st = new StringTokenizer(input, " ");
						if (st.countTokens() == 2) {
							int id;
							try {
								id = Integer.parseInt(st.nextToken());
							} catch (NumberFormatException nfe) {
								Log.err("Received malformed query (no query number)");
								out.println("INVALID");
								connection.close();
								continue;
							}

							String word = st.nextToken();
							Log.out(String.format("Query word: %s", word));
							if (spellingServer.isInList(word)) {
								Log.out("  Word is spelled correctly");
								out.println(String.format("%s OK", id));
							} else {
								String logMessage = "  Word is spelled incorrectly, ";
								String response = String.format("%s NO", id);
								Collection<String> closeWords = spellingServer.getCloseWords(word);
								if (closeWords.size() > 0) {
									logMessage += closeWords.size() + " suggestions: ";
									for (String closeWord : closeWords) {
										logMessage += " " + closeWord;
										response += " " + closeWord;
									}
									logMessage += ".";
								} else
									logMessage += "no suggestions.";
								Log.out(logMessage);
								out.println(response);
							}
						} else {
							Log.err("Received malformed query (wrong number of arguments)");
							out.println("INVALID");
							connection.close();
							continue;
						}
					}
					out.println("GOODBYE");
					connection.close();
					Log.out("Connection closed normally.");
				} catch (Exception e) {
					e.printStackTrace();
				}
		} catch (BindException be) {
			Log.err("Could not bind to socket");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
