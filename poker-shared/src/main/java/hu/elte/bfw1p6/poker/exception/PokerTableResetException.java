package hu.elte.bfw1p6.poker.exception;

/**
 * A póker játékasztal nem indítható újra kivétel.
 * @author feher
 *
 */
public class PokerTableResetException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public PokerTableResetException(String msg) {
		super(msg);
	}
}
