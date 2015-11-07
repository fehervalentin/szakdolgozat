package hu.elte.bfw1p6.poker.command.holdem;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemPlayerCommandType;

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
	
	@Override
	public String getCommandType() {
		return playerCommandType.name();
	}

	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	public void setUpCheckCommand() {
		this.playerCommandType = HoldemPlayerCommandType.CHECK;
	}

	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = HoldemPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	public void setUpFoldCommand(int tempNth) {
		this.playerCommandType = HoldemPlayerCommandType.FOLD;
		this.whosQuit = tempNth;
	}

	public void setUpQuitCommand(int youAreNth) {
		this.playerCommandType = HoldemPlayerCommandType.QUIT;
		this.whosQuit = youAreNth;
	}
	
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
}
