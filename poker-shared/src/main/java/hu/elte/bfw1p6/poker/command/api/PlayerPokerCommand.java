package hu.elte.bfw1p6.poker.command.api;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.type.api.PlayerPokerCommandType;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public abstract class PlayerPokerCommand<T extends PlayerPokerCommandType<T>> implements PokerCommand {
	
	private static final long serialVersionUID = -2991066310623664425L;

	/**
	 * Az utasítás típusa.
	 */
	protected PlayerPokerCommandType<T> type;
	
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

	@Override
	public int getWhosOn() {
		return whosOn;
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
	
	public PokerCommandType<T> getType() {
		return type;
	}
}
