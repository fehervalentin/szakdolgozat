package hu.elte.bfw1p6.poker.exception;

/**
 * Hibás User hiba a póker játékban.
 * @author feher
 *
 */
public class PokerInvalidUserException extends Exception {
	
	private static final long serialVersionUID = -1814186817381041510L;

	public PokerInvalidUserException(String msg) {
		super(msg);
	}
}