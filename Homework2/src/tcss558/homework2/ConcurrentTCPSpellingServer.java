package tcss558.homework2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is a TCP server that implements the TCSS598 Spelling Protocol. It
 * allows a specified number of concurrent connections to access the service, or
 * 10 if no specific limit is provided.
 * 
 * If the number of concurrent connections exceed the maximul allowed, the rest
 * will queue up and be served when available.
 * 
 * @author dmac
 * 
 */
public class ConcurrentTCPSpellingServer {
	/**
	 * usage: java ConcurrentTCPSpellingServer <port> <file location> [<max
	 * connections>]
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConcurrentTCPSpellingServer server = newServer(args);
			server.run();
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
		}
	}

	/**
	 * Builds a {@link ConcurrentTCPSpellingServer} object while validating inputs
	 * 
	 * @param args
	 *          command-line parameters
	 * @return the ConcurrentTCPSpellingServer object
	 * @throws IllegalArgumentException
	 *           The human-readable message with validation errors
	 */
	public static ConcurrentTCPSpellingServer newServer(String[] args) throws IllegalArgumentException {
		if (args.length < 2)
			throw new IllegalArgumentException("usage: java ConcurrentTCPSpellingServer <port> <file location> [<max connections>]");

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
			maxConcurrentSessions = 10;
		else
			try {
				maxConcurrentSessions = Integer.parseInt(args[2]);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Connection count limit must be an integer");
			}

		return newServer(port, wordList, maxConcurrentSessions);
	}

	/**
	 * Builds a {@link ConcurrentTCPSpellingServer} object while validating inputs
	 * 
	 * @param port
	 *          the port number to connect to
	 * @param wordList
	 *          the dictionary
	 * @param maxConcurrentSessions
	 *          total number of concurrent sessions allowed.
	 * @return
	 */
	public static ConcurrentTCPSpellingServer newServer(int port, WordList wordList, int maxConcurrentSessions) {
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

	/**
	 * Runs the server indefinitely
	 */
	public void run() {
		Log.out("Starting server with %d concurrent connections", getMaxConcurrentSessions());
		try (ServerSocket serverSocket = new ServerSocket(getPort())) {
			while (true) {
				Log.out(String.format("Server socket opened on port %s", serverSocket.getLocalPort()));
				Socket socket = serverSocket.accept();
				Log.out(String.format("Accepted connection from %s", socket.getRemoteSocketAddress()));
				threadPool.execute(new TCPSpellingWorker(wordList, socket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}