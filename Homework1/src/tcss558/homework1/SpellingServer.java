package tcss558.homework1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpellingServer {
	private int port;

	private WordList wordList;

	public SpellingServer(String[] args) throws ArgumentException {
		if (args.length != 2)
			throw new ArgumentException("usage: bla bla bla");

		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			throw new ArgumentException("port must be an integer", nfe);
		}

		try {
			Log.out("Loading word list.");
			wordList = new WordList(args[1]);
			Log.out("Word list loaded successfully.");
		} catch (IOException ioe) {
			throw new ArgumentException(String.format("file '%s' could not be found", args[1]), ioe);
		}
	}

	public int getPort() {
		return port;
	}

	public boolean isInList(String word) {
		return wordList.isInList(word);
	}

	public Collection<String> getCloseWords(String word){
		return wordList.getCloseWords(word);
	}
	
	public Collection<String> getCloseWords(String word, int maxResults) {
		Collection<String> closeWords = getCloseWords(word);
		if (closeWords.size() <= maxResults) {
			return closeWords;
		}

		List<String> truncatedList = new ArrayList<String>(Math.min(maxResults, closeWords.size()));

		for (String closeWord : closeWords) {
			if (truncatedList.size() >= maxResults)
				break;
			truncatedList.add(closeWord);
		}

		return truncatedList;
	}

	public class ArgumentException extends Throwable {

		public ArgumentException(String message) {
			super(message);
		}

		public ArgumentException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Serializable classes apparently must declare a static final
		 * serialVersionUID field of type long... or so Eclipse says
		 */
		private static final long serialVersionUID = 3015892312626097909L;
	}
}
