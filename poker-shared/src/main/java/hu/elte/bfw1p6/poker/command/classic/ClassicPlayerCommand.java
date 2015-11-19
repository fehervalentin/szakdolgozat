package hu.elte.bfw1p6.poker.command.classic;

import java.math.BigDecimal;
import java.util.List;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicPlayerCommandType;

/**
 * A játékosok utasításai classic játékstílus esetén.
 * @author feher
 *
 */
public class ClassicPlayerCommand extends PlayerCommand {

	private static final long serialVersionUID = -3424914496460268663L;

	/**
	 * A cserélendő kártyalapok sorszámai.
	 */
	private List<Integer> markedCards;
	
	/**
	 * Az utasítás típusa.
	 */
	private ClassicPlayerCommandType playerCommandType;

	@Override
	public String getCommandType() {
		return playerCommandType.name();
	}
	
	@Override
	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	@Override
	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	@Override
	public void setUpCheckCommand() {
		this.playerCommandType = ClassicPlayerCommandType.CHECK;
	}

	@Override
	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = ClassicPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	@Override
	public ClassicPlayerCommand setUpFoldCommand(int youAreNth) {
		this.playerCommandType = ClassicPlayerCommandType.FOLD;
		this.whosQuit = youAreNth;
		return this;
	}

	@Override
	public ClassicPlayerCommand setUpQuitCommand(String sender, int youAreNth) {
		this.playerCommandType = ClassicPlayerCommandType.QUIT;
		this.sender = sender;
		this.whosQuit = youAreNth;
		return this;
	}
	
	/**
	 * Ha egy játékos CHANGE típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param tempNth a játékos sorszáma
	 */
	public void setUpChangeCommand(List<Integer> markedCards) {
		this.markedCards = markedCards;
		this.playerCommandType = ClassicPlayerCommandType.CHANGE;
	}
	
	public ClassicPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
	
	public List<Integer> getMarkedCards() {
		return markedCards;
	}
}