package hu.elte.bfw1p6.poker.exception;

public class PokerUnauthenticatedException extends Exception {
	private static final long serialVersionUID = 1L;

	public PokerUnauthenticatedException(String msg) {
		super(msg);
	}
}
