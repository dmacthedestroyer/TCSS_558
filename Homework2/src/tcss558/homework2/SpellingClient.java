package tcss558.homework2;


public class SpellingClient {
	private int port;

	public SpellingClient(String[] args) throws IllegalArgumentException {
		if (args.length < 2)
			throw new IllegalArgumentException("usage: todo");

		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("port number must be an integer", nfe);
		}
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("port number must be between 0 and 65535");
		}
	}

	public int getPort() {
		return port;
	}
}