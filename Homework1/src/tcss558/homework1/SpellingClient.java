package tcss558.homework1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class SpellingClient {
	private InetAddress address;
	private int port;
	private Iterable<String> words;
	
	private SpellingClientArgumentException createException(String message, Throwable cause) {
		return new SpellingClientArgumentException(message, cause);
	}

	public SpellingClient(String[] args) throws SpellingClientArgumentException {
		if (args.length < 3)
			throw new IllegalArgumentException("usage: bla bla bla");

		try {
			address = InetAddress.getByName(args[0]);
		} catch (UnknownHostException uhe) {
			throw createException(String.format("host '%s' is unreachable", args[0]), uhe);
		}

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			throw createException("port number must be an integer", nfe);
		}

		words = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
	}
	
	public InetAddress getAddress(){
		return address;
	}
	
	public int getPort(){
		return port;
	}
	
	public Iterable<String> getWords(){
		return words;
	}
}