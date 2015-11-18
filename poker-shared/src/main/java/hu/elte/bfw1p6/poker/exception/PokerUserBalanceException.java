package hu.elte.bfw1p6.poker.exception;

/**
 * User egyenleg hiba.
 * @author feher
 *
 */
public class PokerUserBalanceException extends Exception {

	private static final long serialVersionUID = 2329700386791348251L;

	public PokerUserBalanceException(String msg) {
		super(msg);
	}
}