package hu.elte.bfw1p6.poker.command.holdem;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemPlayerCommandType;

/**
 * A játékosok utasításai holdem játékstílus esetén.
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

	@Override
	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	@Override
	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	@Override
	public void setUpCheckCommand() {
		this.playerCommandType = HoldemPlayerCommandType.CHECK;
	}

	@Override
	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = HoldemPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	@Override
	public void setUpFoldCommand(int tempNth) {
		this.playerCommandType = HoldemPlayerCommandType.FOLD;
		this.whosQuit = tempNth;
	}

	@Override
	public HoldemPlayerCommand setUpQuitCommand(String sender, int youAreNth) {
		this.playerCommandType = HoldemPlayerCommandType.QUIT;
		this.sender = sender;
		this.whosQuit = youAreNth;
		return this;
	}
	
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
}