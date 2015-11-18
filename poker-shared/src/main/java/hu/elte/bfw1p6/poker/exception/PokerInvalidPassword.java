package hu.elte.bfw1p6.poker.exception;

/**
 * Hibás jelszó a póker játékban.
 * @author feher
 *
 */
public class PokerInvalidPassword extends Exception{

	private static final long serialVersionUID = 798680641632769473L;

	public PokerInvalidPassword(String msg) {
		super(msg);
	}
}