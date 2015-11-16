package hu.elte.bfw1p6.poker.command.classic;

import java.math.BigDecimal;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicPlayerCommandType;

/**
 * A játékosok utasításai classic játékstílus esetén.
 * @author feher
 *
 */
public class ClassicPlayerCommand extends PlayerCommand {

	private static final long serialVersionUID = -3424914496460268663L;

	private Card[] cards;
	
	private List<Integer> markedCards;
	
	/**
	 * Az utasítás típusa.
	 */
	private ClassicPlayerCommandType playerCommandType;

	@Override
	public String getCommandType() {
		return playerCommandType.name();
	}
	
	/**
	 * Ha egy játékos CALL típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 */
	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	/**
	 * Ha egy játékos BLIND típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a vak mértéke
	 */
	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	/**
	 * Ha egy játékos CHECK típusú utasítást küld, akkor ezt a metódust kell használni.
	 */
	public void setUpCheckCommand() {
		this.playerCommandType = ClassicPlayerCommandType.CHECK;
	}

	/**
	 * Ha egy játékos RAISE típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 * @param raiseAmount az emelendő tét
	 */
	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = ClassicPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	/**
	 * Ha egy játékos FOLD típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param youAreNth a játékos sorszáma
	 */
	public void setUpFoldCommand(int youAreNth) {
		this.playerCommandType = ClassicPlayerCommandType.FOLD;
		this.whosQuit = youAreNth;
	}

	/**
	 * Ha egy játékos QUIT típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param tempNth a játékos sorszáma
	 * @return this
	 */
	public ClassicPlayerCommand setUpQuitCommand(int youAreNth) {
		this.playerCommandType = ClassicPlayerCommandType.QUIT;
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
	
	public Card[] getCards() {
		return cards;
	}
	
	public List<Integer> getMarkedCards() {
		return markedCards;
	}
}