import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.SortedSet;

public class RMISpellingClient {
	public static void main(String[] args) throws RemoteException {
		RMISpellingClient client;
		try {
			client = RMISpellingClient.newRMISPellingClient(args);
		} catch (IllegalArgumentException iae) {
			Log.err(iae.getMessage());
			return;
		}

		RemoteSpelling rs = client.getRemoteSpelling();

		for (int i = 3; i < args.length; i++) {
			String s = args[i];
			Log.out("Querying service for %s", s);
			SortedSet<String> result = rs.check(s);
			if (result == null)
				Log.out("%s is spelled correctly", s);
			else
			{
				String prefix = String.format("%s is spelled incorrectly, ", s);
				if(result.size() == 0)
					Log.out(prefix + "no suggestions.");
				else Log.out(prefix + "suggestions: " + Utility.join(result, ", "));
			}
		}
	}

	public static RMISpellingClient newRMISPellingClient(String[] args) throws IllegalArgumentException {
		if (args.length < 4)
			throw new IllegalArgumentException("Usage: java RMISpellingClient <hostname> <port> <service-name> <word> [<word> ...]");

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

	public static RMISpellingClient newRMISPellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) {
		if (hostPort < 0 || hostPort > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}

		return new RMISpellingClient(hostAddress, hostPort, spellingServiceRegisteredName);
	}

	private RMISpellingClient(InetAddress hostAddress, int hostPort, String spellingServiceRegisteredName) {
		this.address = hostAddress;
		this.hostPort = hostPort;
		this.spellingServiceRegisteredName = spellingServiceRegisteredName;

		try {
			Registry registry = LocateRegistry.getRegistry(hostAddress.getHostName(), hostPort);
			remoteSpelling = (RemoteSpelling) registry.lookup(spellingServiceRegisteredName);
		} catch (NotBoundException e) {
			Log.err("Service '%s' is not bound to host '%s:%s'", spellingServiceRegisteredName, hostAddress, hostPort);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}

	private final InetAddress address;

	private final int hostPort;

	private final String spellingServiceRegisteredName;

	private RemoteSpelling remoteSpelling;

	public RemoteSpelling getRemoteSpelling() {
		return remoteSpelling;
	}

	public void QueryWord(String word) {
		Log.out("todo: query the word");
	}
}
