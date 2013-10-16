package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

		while (true)
			try (ServerSocket serverSocket = new ServerSocket(spellingServer.getPort());
					Socket connection = serverSocket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					PrintWriter out = new PrintWriter(connection.getOutputStream(), true)) {
				String input;

				while ((input = in.readLine()) != null) {
					Log.out("RECEIVED: " + input);

					out.println(input.toUpperCase());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
