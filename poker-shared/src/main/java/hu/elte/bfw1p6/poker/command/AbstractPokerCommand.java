package hu.elte.bfw1p6.poker.command;

import hu.elte.bfw1p6.poker.command.api.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public class AbstractPokerCommand<T extends PokerCommandType<T>> implements PokerCommand<T> {
	
	private static final long serialVersionUID = 5314949936432890389L;

	/**
	 * Az utasítás típusa.
	 */
	protected T type;
	
	/**
	 * Ki következik éppen.
	 */
	protected int whosOn;

	@Override
	public int getWhosOn() {
		return whosOn;
	}

	@Override
	public T getType() {
		return type;
	}

	@Override
	public void setType(T type) {
		this.type = type;
	}

}
