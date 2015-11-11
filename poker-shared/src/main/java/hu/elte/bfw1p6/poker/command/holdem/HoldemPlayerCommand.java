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

	/**
	 * Ha egy játékos CALL típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 */
	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	/**
	 * Ha egy játékos BLIND típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a vak mértéke
	 */
	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = HoldemPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	/**
	 * Ha egy játékos CHECK típusú utasítást küld, akkor ezt a metódust kell használni.
	 */
	public void setUpCheckCommand() {
		this.playerCommandType = HoldemPlayerCommandType.CHECK;
	}

	/**
	 * Ha egy játékos RAISE típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 * @param raiseAmount az emelendő tét
	 */
	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = HoldemPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	/**
	 * Ha egy játékos FOLD típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param youAreNth a játékos sorszáma
	 */
	public void setUpFoldCommand(int tempNth) {
		this.playerCommandType = HoldemPlayerCommandType.FOLD;
		this.whosQuit = tempNth;
	}

	/**
	 * Ha egy játékos QUIT típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param tempNth a játékos sorszáma
	 */
	public void setUpQuitCommand(int youAreNth) {
		this.playerCommandType = HoldemPlayerCommandType.QUIT;
		this.whosQuit = youAreNth;
	}
	
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
}