package tcss558.homework1;

public class SpellingClientArgumentException extends Throwable {
	/**
	 * Serializable classes apparently must declare a static final serialVersionUID field of type long... or so Eclipse says
	 */
	private static final long serialVersionUID = 644756536510304257L;

	public SpellingClientArgumentException(String message){
		super(message);
	}
	
	public SpellingClientArgumentException(String message, Throwable cause){
		super(message, cause);
	}
}
