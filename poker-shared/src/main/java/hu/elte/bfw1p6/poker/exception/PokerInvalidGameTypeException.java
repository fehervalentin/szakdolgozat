package hu.elte.bfw1p6.poker.exception;

/**
 * Póker játék nem létező játékstílus hibája.
 * @author feher
 *
 */
public class PokerInvalidGameTypeException extends Exception {

	private static final long serialVersionUID = -8217146297149056650L;

	public PokerInvalidGameTypeException(String msg) {
		super(msg);
	}

}
