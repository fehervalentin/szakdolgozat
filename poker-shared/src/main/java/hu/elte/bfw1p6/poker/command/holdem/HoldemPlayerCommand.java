package hu.elte.bfw1p6.poker.command.holdem;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat
 * @author feher
 *
 */
public class HoldemPlayerCommand extends PlayerCommand {

	private static final long serialVersionUID = -4550840731511497967L;

	/**
	 * Az utasítás típusa.
	 */
	private HoldemPlayerCommandType playerCommandType;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param callAmount CALL esetén a megadandó összeg
	 * @param raiseAmount RAISE esetén az emelendő összeg
	 */
	public HoldemPlayerCommand(HoldemPlayerCommandType playerCommandType, BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
		this.playerCommandType = playerCommandType;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
		this.whosQuit = whosQuit;
	}
	
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
}
