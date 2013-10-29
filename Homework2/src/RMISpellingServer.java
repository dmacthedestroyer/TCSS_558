import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.SortedSet;

public class RMISpellingServer implements RemoteSpelling, Runnable {

	public static void main(String[] args) {
		try {
			RMISpellingServer server = newRMISpellingServer(args);
			server.run();
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			Log.out(CommandLineInstructions);
		}
	}

	public final static String CommandLineInstructions = "Usage: java RMISpellingClient <port> <service-name> <word-list>";

	public static RMISpellingServer newRMISpellingServer(String[] args) throws IllegalArgumentException {
		if (args.length != 3)
			throw new IllegalArgumentException("Invalid command line options");

		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("port number must be an integer", nfe);
		}

		String registeredName = args[1];
		WordList wordList;

		try {
			Log.out("Loading word list.");
			wordList = new WordList(args[2]);
			Log.out("Word list loaded successfully.");
		} catch (IOException ioe) {
			throw new IllegalArgumentException(String.format("file '%s' could not be found", args[1]), ioe);
		}

		return newRMISpellingServer(port, registeredName, wordList);
	}

	public static RMISpellingServer newRMISpellingServer(int port, String registeredName, WordList wordList) throws IllegalArgumentException {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}

		return new RMISpellingServer(port, registeredName, wordList);
	}

	private final int port;

	private final String registeredName;

	private final WordList wordList;

	private RMISpellingServer(int port, String registeredName, WordList wordList) {
		this.port = port;
		this.registeredName = registeredName;
		this.wordList = wordList;
	}

	@Override
	public SortedSet<String> check(String the_word) throws RemoteException {
		synchronized (wordList) {
			if (wordList.isInList(the_word)) {
				Log.out("%s is spelled correctly", the_word);
				return null;
			}
			String prefix = String.format("%s is spelled incorrectly, ", the_word);
			SortedSet<String> closeWords = wordList.getCloseWords(the_word);
			if (closeWords.size() == 0)
				Log.out(prefix + "no suggestions");
			else
				Log.out(prefix + "suggestions: " + Utility.join(closeWords, ", "));
			return closeWords;
		}
	}

	@Override
	public void add(String the_word) throws RemoteException {
		wordList.add(the_word);
		Log.out("'%s' has been added to the dictionary", the_word);
	}

	@Override
	public void remove(String the_word) throws RemoteException {
		wordList.remove(the_word);
		Log.out("'%s' has been removed from the dictionary", the_word);
	}

	public void run() {
		try {
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(registeredName, UnicastRemoteObject.exportObject(this, 0));
			Log.out("Exported spelling service as '%s' on registry port %s", registeredName, port);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}
}
