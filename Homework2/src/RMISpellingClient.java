import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.SortedSet;

/**
 * Java RMI client for the TCSS 598 Spelling Protocol.
 * 
 * @author dmac
 * 
 */
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

	/**
	 * Basic output for posting instructions on command-line use.
	 */
	public final static String CommandLineInstructions = "Usage: java RMISpellingClient <hostname> <port> <service-name> <word> [<word> ...]";

	/**
	 * Creates a new instance of the RMISpellingClient with a method signature
	 * useful for command-line applications.
	 * 
	 * @param args
	 *          array on inputs required to properly start the server
	 * @return the RMISpellingClient instance
	 * @throws IllegalArgumentException
	 *           if the args are entered incorrectly
	 */
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

	/**
	 * Creates a new instance of the RMISpellingClient.
	 * 
	 * @param hostAddress
	 *          address of the RMI server
	 * @param hostPort
	 *          port of the RMI server
	 * @param spellingServiceRegisteredName
	 *          name of the resource
	 * @return the new RMISpellingClient instance
	 * @throws RemoteException
	 *           if a remote exception occurs
	 * @throws NotBoundException
	 *           if an attempt is made to lookup or unbind in the registry a name
	 *           that has no associated binding.
	 */
	public static RMISpellingClient newRMISPellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) throws RemoteException, NotBoundException {
		if (hostPort < 0 || hostPort > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}

		return new RMISpellingClient(hostAddress, hostPort, spellingServiceRegisteredName);
	}

	private RMISpellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) throws RemoteException, NotBoundException {
		remoteSpelling = getRemoteSpellingFromRMI(hostAddress.getHostName(), hostPort, spellingServiceRegisteredName);
	}

	/**
	 * gets a remote {@link RemoteSpelling} object from the given host, port and
	 * name
	 * 
	 * @param hostName
	 *          url of RMI host
	 * @param hostPort
	 *          port number
	 * @param spellingServiceRegisteredName
	 *          name of RMI resource
	 * @return the remote object
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static RemoteSpelling getRemoteSpellingFromRMI(String hostName, int hostPort, String spellingServiceRegisteredName) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(hostName, hostPort);
		return (RemoteSpelling) registry.lookup(spellingServiceRegisteredName);
	}

	private RemoteSpelling remoteSpelling;

	/**
	 * Queries a remoted {@link RemoteSpelling} object for the given word
	 * 
	 * @param word
	 *          The word to check.
	 * @return a sorted set of suggestions (which may be empty) if the word is
	 *         spelled incorrectly, or null if the word is spelled correctly.
	 * @throws RemoteException
	 *           if there is a problem completing the method call.
	 */
	public SortedSet<String> check(String word) throws RemoteException {
		return remoteSpelling.check(word);
	}
}
