package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import tcss558.homework1.Log;
import tcss558.homework1.SpellingClient;
import tcss558.homework1.SpellingClientArgumentException;

public class TCPSpellingClient {
	public static void main(String[] args) {
		SpellingClient client;
		try {
			client = new SpellingClient(args);
		} catch (SpellingClientArgumentException e) {
			Log.err(e.getMessage());
			return;
		}

		try (Socket clientSocket = new Socket(client.getAddress(), client.getPort());
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			Log.out(String.format("Socket opened on port %s", clientSocket.getPort()));
			clientSocket.setSoTimeout(1000);

			int i = 1;
			for (String output : client.getWords()) {
				try {
					Log.out(String.format("Querying server (%s)", output));
					out.println(String.format("%s %s", i, output));
					StringTokenizer input = new StringTokenizer(in.readLine(), " ");

					if (input.countTokens() >= 2) {
						String id = input.nextToken();
						if (id.equals(""+i)) {
							String status = input.nextToken();
							if (status.equals("OK"))
								Log.out(String.format("  %s is spelled correctly.", output));
							else if (status.equals("NO")) {
								String logMessage = String.format("  %s is spelled incorrectly.", output);
								List<String> suggestions = new ArrayList<String>();
								while (input.hasMoreTokens())
									suggestions.add(input.nextToken());
								if (suggestions.size() == 0)
									logMessage += "There are no suggested words";
								else {
									logMessage += String.format("There are %s suggested words: ", suggestions.size());
									for (String s : suggestions)
										logMessage += " " + s;
								}
								Log.out(logMessage);
							} else
								Log.err("Received malformed input from server: status is neither 'OK' nor 'NO'");
						} else
							Log.err(String.format("Received malformed input from server: unrecognized id '%s'", id));
					} else
						Log.err("Received malformed input from server");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
			out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
