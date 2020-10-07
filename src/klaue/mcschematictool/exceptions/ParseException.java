package klaue.mcschematictool.exceptions;

/**
 * Exception for parsing errors
 * @author klaue
 *
 */
public class ParseException extends Exception {
	/**
	 * @param errorMessage 
	 * 
	 */
	public ParseException(String errorMessage) {
		super(errorMessage);
	}
	/**
	 * @param t
	 */
	public ParseException(Throwable t) {
		super(t);
	}
	
	/**
	 * @param t
	 * @param errorMessage 
	 */
	public ParseException(Throwable t, String errorMessage) {
		super(errorMessage, t);
	}
}