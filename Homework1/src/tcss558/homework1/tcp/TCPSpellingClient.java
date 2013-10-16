package tcss558.homework1.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import tcss558.homework1.Log;

public class TCPSpellingClient {
	public static void main(String[] args) {
		try (Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			clientSocket.setSoTimeout(1000);

			for (int i = 2; i < args.length; i++) {
				try {
					String output = args[i];
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
