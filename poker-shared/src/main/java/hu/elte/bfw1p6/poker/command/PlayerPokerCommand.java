package hu.elte.bfw1p6.poker.command;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public class PlayerPokerCommand<T extends PokerCommandType<T>> extends AbstractPokerCommand<T> {

	private static final long serialVersionUID = -8652099535754017708L;
	
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
	 * Ki lépett ki.
	 */
	protected int whosQuit;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param callAmount CALL esetén a megadandó összeg
	 * @param raiseAmount RAISE esetén az emelendő összeg
	 */
	public PlayerPokerCommand(BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
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
}
