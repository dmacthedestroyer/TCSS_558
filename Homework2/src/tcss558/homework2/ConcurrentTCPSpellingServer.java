package tcss558.homework2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTCPSpellingServer {
	public static void main(String[] args) {
		try {
			ConcurrentTCPSpellingServer server = buildServer(args);
			server.run();
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
		}
	}

	public static ConcurrentTCPSpellingServer buildServer(String[] args) throws IllegalArgumentException {
		if (args.length < 2)
			throw new IllegalArgumentException("usage: todo");

		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("port number must be an integer", nfe);
		}

		WordList wordList;
		try {
			Log.out("Loading word list.");
			wordList = new WordList(args[1]);
			Log.out("Word list loaded successfully.");
		} catch (IOException ioe) {
			throw new IllegalArgumentException(String.format("file '%s' could not be found", args[1]), ioe);
		}

		int maxConcurrentSessions;
		if (args.length < 3)
			maxConcurrentSessions = 1000;
		else
			try {
				maxConcurrentSessions = Integer.parseInt(args[2]);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Connection count limit must be an integer");
			}

		return buildServer(port, wordList, maxConcurrentSessions);
	}

	public static ConcurrentTCPSpellingServer buildServer(int port, WordList wordList, int maxConcurrentSessions) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}
		if (maxConcurrentSessions <= 0)
			throw new IllegalArgumentException("Connection count limit must be greater than zero");

		return new ConcurrentTCPSpellingServer(port, wordList, maxConcurrentSessions);
	}

	private int port;

	private WordList wordList;

	private int maxConcurrentSessions;

	ExecutorService threadPool;

	private ConcurrentTCPSpellingServer(int port, WordList wordList, int maxConcurrentSessions) {
		this.port = port;
		this.wordList = wordList;
		this.maxConcurrentSessions = maxConcurrentSessions;
		threadPool = Executors.newFixedThreadPool(maxConcurrentSessions);
	}

	public int getPort() {
		return port;
	}

	public int getMaxConcurrentSessions() {
		return maxConcurrentSessions;
	}

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(getPort())) {
			while (true) {
				Log.out(String.format("Server socket opened on port %s", serverSocket.getLocalPort()));
				try (Socket socket = serverSocket.accept()) {
					Log.out(String.format("Accepted connection from %s", socket.getRemoteSocketAddress()));
					threadPool.execute(new TCPSpellingWorker(wordList, socket));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}