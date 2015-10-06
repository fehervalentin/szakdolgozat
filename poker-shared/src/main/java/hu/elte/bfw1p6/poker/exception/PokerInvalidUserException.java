package hu.elte.bfw1p6.poker.exception;

public class PokerInvalidUserException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public PokerInvalidUserException(String msg) {
		super(msg);
	}
}
