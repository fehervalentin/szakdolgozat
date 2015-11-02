package hu.elte.bfw1p6.poker.command;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.api.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public class PlayerPokerCommand<T extends PokerCommandType<T>> implements PokerCommand<T> {

	private static final long serialVersionUID = -8652099535754017708L;

	/**
	 * Az utasítás típusa.
	 */
	protected T type;
	
	/**
	 * A megadandó tét.
	 */
	protected BigDecimal callAmount;
	
	/**
	 * Az emelendő tét.
	 */
	protected BigDecimal raiseAmount;
	
	/**
	 * Az üzenetet küldő játékos neve.
	 */
	protected String sender;
	
	/**
	 * Ki van éppen soron.
	 */
	protected int whosOn;
	
	/**
	 * Ki lépett ki.
	 */
	protected int whosQuit;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param callAmount CALL esetén a megadandó összeg
	 * @param raiseAmount RAISE esetén az emelendő összeg
	 */
	public PlayerPokerCommand(T type, BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
		this.type = type;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
		this.whosQuit = whosQuit;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setWhosOn(int whosOn) {
		this.whosOn = whosOn;
	}
	
	public int getWhosQuit() {
		return whosQuit;
	}

	public BigDecimal getCallAmount() {
		return callAmount;
	}

	public BigDecimal getRaiseAmount() {
		return raiseAmount;
	}

	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	@Override
	public T getType() {
		return type;
	}
}
