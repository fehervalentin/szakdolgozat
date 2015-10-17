package hu.elte.bfw1p6.poker.exception;

public class PokerInvalidPassword extends Exception{
	private static final long serialVersionUID = 1L;

	public PokerInvalidPassword(String msg) {
		super(msg);
	}
}
