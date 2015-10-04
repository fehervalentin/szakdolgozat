package hu.elte.bfw1p6.poker.exception;

public class PokerInvalidUserException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "Invalid username: ";

	public PokerInvalidUserException(String msg) {
		super(PREFIX + msg);
	}
}
