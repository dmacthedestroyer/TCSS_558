package tcss558.homework2;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.StringTokenizer;

public class TCPSpellingWorker implements Runnable {

	private Socket socket;

	private WordList wordList;

	public TCPSpellingWorker(WordList wordList, Socket socket) {
		this.wordList = wordList;
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), Charset.forName("US-ASCII")))) {
			String input;

			while (!socket.isClosed() && (input = "" + in.readLine()).length() > 0) {
				StringTokenizer st = new StringTokenizer(input, " ");
				if (st.countTokens() == 2) {
					int id;
					try {
						id = Integer.parseInt(st.nextToken());
					} catch (NumberFormatException nfe) {
						Log.err("Received malformed query (no query number)");
						out.println("INVALID");
						socket.close();
						continue;
					}

					String word = st.nextToken();
					Log.out(String.format("Query word: %s", word));
					if (wordList.isInList(word)) {
						Log.out("  Word is spelled correctly");
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
						Log.out(logMessage);
						out.println();
					}
				} else {
					Log.err("Received malformed query (wrong number of arguments)");
					out.println("INVALID");
					socket.close();
					continue;
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