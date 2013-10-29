import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedSet;

/**
 * quick 'n dirty class to do simple interaction with the RMI server/client
 * @author dmac
 *
 */
public class RMIServerLoadTest {

	public static void main(String[] args) throws IOException {
		RMIServerLoadTest t = new RMIServerLoadTest(args[0], Integer.parseInt(args[1]), new WordList(args[2]), Boolean.parseBoolean(args[3]));
		t.run();
	}

	private final String address;

	private final int port;

	private final WordList wordList;

	private final boolean shouldStartServer;
	
	public static final String rmiName = "serverLoadTest";

	public RMIServerLoadTest(String address, int port, WordList wordList, boolean shouldStartServer) {
		this.address = address;
		this.port = port;
		this.wordList = wordList;
		this.shouldStartServer = shouldStartServer;
	}

	public void run() {
		if(shouldStartServer)
			new Thread(RMISpellingServer.newRMISpellingServer(port, rmiName, wordList)).run();

		try (Scanner scanner = new Scanner(System.in);) {
			String input;
			while ((input = scanner.nextLine()).length() > 0) {
				char command = input.charAt(0);
				input = input.substring(1);

				switch (command) {
				case '+':
					addWord(input);
					break;
				case '-':
					removeWord(input);
					break;
				case '?':
					checkWord(input);
					break;
				case '!':
					doCrazyStuff(input);
					break;
				default:
					System.out.println("invalid command");
				}
			}
			System.out.println("Goodbye");
		}
	}

	public RemoteSpelling getClient() throws RemoteException, UnknownHostException, NotBoundException {
		return RMISpellingClient.getRemoteSpellingFromRMI(address, port, rmiName);
	}

	public void addWord(final String word) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					getClient().add(word);
				} catch (RemoteException
						| UnknownHostException
						| NotBoundException e) {
					e.printStackTrace();
				}
			}
		}).run();
	}

	public void removeWord(final String word) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					getClient().remove(word);
				} catch (RemoteException
						| UnknownHostException
						| NotBoundException e) {
					e.printStackTrace();
				}
			}
		}).run();
	}

	public void checkWord(final String word) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					getClient().check(word);
				} catch (RemoteException
						| UnknownHostException
						| NotBoundException e) {
					e.printStackTrace();
				}
			}
		}).run();
	}

	public void doCrazyStuff(final String word) {
		for (int i = 0; i < 1000; i++) {
			if (i % 2 == 0)
				checkWord(jumble(word));
			if (i % 3 == 0)
				addWord(jumble(word));
			if (i % 4 == 0)
				removeWord(jumble(word));
		}
	}

	private static final Random random = new Random();

	private static String jumble(String word) {
		int i = random.nextInt(word.length());
		return word.substring(0, i) + word.substring(i + 1, word.length());
	}
}
