package hu.elte.bfw1p6.poker.command.holdem;

import java.io.Serializable;
import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat
 * @author feher
 *
 */
public class PlayerHoldemCommand implements PokerCommand, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HoldemPlayerCommandType playerCommandType;
	private BigDecimal callAmount;
	private BigDecimal raiseAmount;
	
	private String sender;
	
	private int whosOn;
	
	private int whosQuit;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param callAmount CALL esetén a megadandó összeg
	 * @param raiseAmount RAISE esetén az emelendő összeg
	 */
	public PlayerHoldemCommand(HoldemPlayerCommandType playerCommandType, BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = playerCommandType;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}
	
	public PlayerHoldemCommand(HoldemPlayerCommandType playerCommandType, int whosQuit) {
		this.playerCommandType = playerCommandType;
		this.whosOn = whosQuit;
	}

	public BigDecimal getCallAmount() {
		return callAmount;
	}
	
	/**
	 * Emelés esetén az emelendő összeg.
	 * @return az emelendő összeg
	 */
	public BigDecimal getRaiseAmount() {
		return raiseAmount;
	}
	
	/**
	 * 
	 * @return A játékos által választott utasítás
	 */
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
	
	/**
	 * 
	 * @return Az utasítást küldő felhasználó neve
	 */
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

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
