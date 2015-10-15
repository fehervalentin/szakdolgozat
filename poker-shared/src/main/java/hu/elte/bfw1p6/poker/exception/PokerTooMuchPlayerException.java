package hu.elte.bfw1p6.poker.exception;

public class PokerTooMuchPlayerException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public PokerTooMuchPlayerException(String msg) {
		super(msg);
	}
}
