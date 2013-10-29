import java.util.Collection;

/**
 * General grab bag of various utility methods
 * @author dmac
 *
 */
public class Utility {
	/**
	 * combines the elements in a collection, with each element separated by the passed argument
	 * @param collection the items to join together
	 * @param separator the string to concatenate between each element
	 * @return the joined string
	 */
	public static <T> String join(Collection<T> collection, String separator) {
		String out = "";

		Boolean firstElement = true;
		for (T t : collection) {
			if (!firstElement)
				out += separator;
			out += t;
			firstElement = false;
		}

		return out;
	}
}
