package hu.elte.bfw1p6.poker.exception;

public class PokerInvalidGameTypeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public PokerInvalidGameTypeException(String msg) {
		super(msg);
	}

}
