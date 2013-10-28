import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.SortedSet;

public class RMISpellingClient {
	public static void main(String[] args) {
		try {
			RMISpellingClient client = RMISpellingClient.newRMISPellingClient(args);

			for (int i = 3; i < args.length; i++) {
				String s = args[i];
				Log.out("Querying service for %s", s);
				SortedSet<String> result = client.check(s);
				if (result == null)
					Log.out("%s is spelled correctly", s);
				else {
					String prefix = String.format("%s is spelled incorrectly, ", s);
					if (result.size() == 0)
						Log.out(prefix + "no suggestions.");
					else
						Log.out(prefix + "suggestions: " + Utility.join(result, ", "));
				}
			}
		} catch (ConnectException ce) {
			Log.err("Connection to server could not be established");
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			Log.out(CommandLineInstructions);
		} catch (NotBoundException nbe) {
			Log.err("The registry '%s' is not bound", nbe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public final static String CommandLineInstructions = "Usage: java RMISpellingClient <hostname> <port> <service-name> <word> [<word> ...]";

	public static RMISpellingClient newRMISPellingClient(String[] args) throws IllegalArgumentException, RemoteException, NotBoundException {
		if (args.length < 4)
			throw new IllegalArgumentException("Invalid command line options");

		InetAddress address;
		try {
			address = InetAddress.getByName(args[0]);
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException(String.format("host '%s' is unreachable", args[0]), uhe);
		}

		int port;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("port number must be an integer", nfe);
		}

		String registeredName = args[2];

		return newRMISPellingClient(address, port, registeredName);
	}

	public static RMISpellingClient newRMISPellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) throws RemoteException, NotBoundException {
		if (hostPort < 0 || hostPort > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}

		return new RMISpellingClient(hostAddress, hostPort, spellingServiceRegisteredName);
	}

	private RMISpellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(hostAddress.getHostName(), hostPort);
		remoteSpelling = (RemoteSpelling) registry.lookup(spellingServiceRegisteredName);
	}

	private RemoteSpelling remoteSpelling;

	public SortedSet<String> check(String word) throws RemoteException {
		return remoteSpelling.check(word);
	}

	public void QueryWord(String word) {
		Log.out("todo: query the word");
	}
}
