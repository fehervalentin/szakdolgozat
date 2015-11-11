package hu.elte.bfw1p6.poker.exception;

/**
 * Autentikációval nem rendelkező user hiba.
 * @author feher
 *
 */
public class PokerUnauthenticatedException extends Exception {

	private static final long serialVersionUID = -7917794646458362272L;

	public PokerUnauthenticatedException(String msg) {
		super(msg);
	}
}
