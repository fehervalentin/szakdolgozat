package hu.elte.bfw1p6.poker.exception;

/**
 * Póker játéktábla nem törölhető kivétel.
 * @author feher
 *
 */
public class PokerTableDeleteException extends Exception {

	private static final long serialVersionUID = -7803300471978980035L;

	public PokerTableDeleteException(String msg) {
		super(msg);
	}
}