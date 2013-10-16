package tcss558.homework1;

import java.io.IOException;

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
			wordList = new WordList(args[1]);
		} catch (IOException ioe) {
			throw new ArgumentException(String.format("file '%s' could not be found", args[1]), ioe);
		}
	}

	public int getPort() {
		return port;
	}

	public WordList getWordList() {
		return wordList;
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
