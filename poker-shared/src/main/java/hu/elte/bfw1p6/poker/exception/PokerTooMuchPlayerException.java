package hu.elte.bfw1p6.poker.exception;

/**
 * Sikertelen csatlakozási kérelem egy asztalhoz.
 * @author feher
 *
 */
public class PokerTooMuchPlayerException extends Exception {
	
	private static final long serialVersionUID = 3132909571693059641L;

	public PokerTooMuchPlayerException(String msg) {
		super(msg);
	}
}
