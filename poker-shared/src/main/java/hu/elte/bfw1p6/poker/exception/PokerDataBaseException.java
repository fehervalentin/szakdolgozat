package hu.elte.bfw1p6.poker.exception;

/**
 * Póker játék adatbázisbeli hibája.
 * @author feher
 *
 */
public class PokerDataBaseException extends Exception {
	
	private static final long serialVersionUID = -4800199707250741145L;

	public PokerDataBaseException(String msg) {
		super(msg);
	}
}