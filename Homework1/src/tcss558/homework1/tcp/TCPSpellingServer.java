package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import tcss558.homework1.Log;

public class TCPSpellingServer {
	public static void main(String[] args) {
		while (true)
			try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
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
