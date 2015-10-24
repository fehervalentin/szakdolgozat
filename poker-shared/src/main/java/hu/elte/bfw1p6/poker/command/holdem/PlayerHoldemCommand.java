package hu.elte.bfw1p6.poker.command.holdem;

import java.io.Serializable;
import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat
 * @author feher
 *
 */
public class PlayerHoldemCommand implements PokerCommand, Serializable {

	private static final long serialVersionUID = -4550840731511497967L;

	/**
	 * Az utasítás típusa.
	 */
	private HoldemPlayerCommandType playerCommandType;
	
	/**
	 * A megadandó tét.
	 */
	private BigDecimal callAmount;
	
	/**
	 * Az emelendő tét.
	 */
	private BigDecimal raiseAmount;
	
	/**
	 * Az üzenetet küldő játékos neve.
	 */
	private String sender;
	
	/**
	 * Ki van éppen soron.
	 */
	private int whosOn;
	
	/**
	 * Ki lépett ki.
	 */
	private int whosQuit;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param callAmount CALL esetén a megadandó összeg
	 * @param raiseAmount RAISE esetén az emelendő összeg
	 */
	public PlayerHoldemCommand(HoldemPlayerCommandType playerCommandType, BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
		this.playerCommandType = playerCommandType;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
		this.whosQuit = whosQuit;
	}

	public BigDecimal getCallAmount() {
		return callAmount;
	}

	public BigDecimal getRaiseAmount() {
		return raiseAmount;
	}
	
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
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
	
}
