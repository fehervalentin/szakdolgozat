package hu.elte.bfw1p6.poker.exception;

public class PokerPlayerAccountInUseException extends Exception {

	private static final long serialVersionUID = 6973126322272584232L;

	public PokerPlayerAccountInUseException(String msg) {
		super(msg);
	}
}
