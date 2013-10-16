package tcss558.homework1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

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

	public boolean isInList(String word){
		return wordList.isInList(word);
	}
	
	public static final int MAX_CLOSE_WORDS_COUNT = 255;
	public static final int MAX_UDP_DATAGRAM_SIZE = 576;
	
	public Collection<String> getCloseWords(String word, int currentDatagramSize){
		SortedSet<String> closeWords = wordList.getCloseWords(word);
		
		List<String> truncatedList = new ArrayList<String>(Math.min(MAX_CLOSE_WORDS_COUNT, closeWords.size()));
		int cumulativeDatagramSize = currentDatagramSize;
		
		for(String closeWord : closeWords){
			if(truncatedList.size() >= MAX_CLOSE_WORDS_COUNT || cumulativeDatagramSize + closeWord.length() + 1 > MAX_UDP_DATAGRAM_SIZE)
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
