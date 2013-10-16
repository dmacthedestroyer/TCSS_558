package tcss558.homework1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class SpellingClient {
	private InetAddress address;
	private int port;
	private Iterable<String> words;

	public SpellingClient(String[] args) throws IllegalArgumentException {
		if (args.length < 3)
			throw new IllegalArgumentException("Usage: java TCPClient <hostname> <port> <word> [<word> ...]");

		try {
			address = InetAddress.getByName(args[0]);
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException(String.format("host '%s' is unreachable", args[0]), uhe);
		}

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("port number must be an integer", nfe);
		}

		words = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public Iterable<String> getWords() {
		return words;
	}
}