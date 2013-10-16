package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
			clientSocket.setSoTimeout(1000);

			for (String output : client.getWords()) {
				try {
					out.println(output);
					Log.out(String.format("%s -> %s", output, in.readLine()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
