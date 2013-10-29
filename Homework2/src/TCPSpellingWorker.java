import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the TCSS Spelling Protocol for a single TCP socket connection to a
 * client.
 * 
 * @author dmac
 * 
 */
public class TCPSpellingWorker implements Runnable {

	private static AtomicInteger idCount = new AtomicInteger();

	private int id;

	private final Socket socket;

	private final WordList wordList;

	private void err(String message) {
		Log.err("" + id + ": " + message);
	}

	private void log(String message) {
		Log.out("" + id + ": " + message);
	}

	private void log(String message, Object... args) {
		Log.out(message, args);
	}

	/**
	 * Creates a new TCPSpellingWorker with the given dictionary and socket
	 * connection.
	 * 
	 * @param wordList
	 * @param socket
	 */
	public TCPSpellingWorker(WordList wordList, Socket socket) {
		this.wordList = wordList;
		this.socket = socket;
		id = idCount.incrementAndGet();
	}

	/**
	 * Reads in lines from the client socket as outlined in the TCSS 598 Spelling
	 * Protocol, and terminates when the connection is closed.
	 */
	@Override
	public void run() {
		Log.out("Connected to client with id %s", id);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), Charset.forName("US-ASCII")), true)) {
			String input;

			while (!socket.isClosed() && (input = in.readLine()).length() > 0) {
				StringTokenizer st = new StringTokenizer(input, " ");
				if (st.countTokens() == 2) {
					int id;
					try {
						id = Integer.parseInt(st.nextToken());
					} catch (NumberFormatException nfe) {
						err("Received malformed query (no id)");
						out.println("INVALID");
						socket.close();
						return;
					}
					if (id < 0) {
						err("Received malformed query (id < 0)");
						out.println("INVALID");
						socket.close();
						return;
					}

					String word = st.nextToken();
					log("Query word: %s", word);
					if (wordList.isInList(word)) {
						log("  Word is spelled correctly");
						out.println(String.format("%s OK", id));
					} else {
						String logMessage = "  Word is spelled incorrectly, ";
						out.print(id);
						out.print(" NO");
						Collection<String> closeWords = wordList.getCloseWords(word);
						if (closeWords.size() > 0) {
							logMessage += closeWords.size() + " suggestions: ";
							for (String closeWord : closeWords) {
								logMessage += " " + closeWord;
								out.print(" " + closeWord);
							}
							logMessage += ".";
						} else
							logMessage += "no suggestions.";
						log(logMessage);
						out.println("");
					}
				} else {
					err("Received malformed query (wrong number of arguments)");
					out.println("INVALID");
					socket.close();
					return;
				}
			}
			out.println("GOODBYE");
			socket.close();
			Log.out("Connection closed normally.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}