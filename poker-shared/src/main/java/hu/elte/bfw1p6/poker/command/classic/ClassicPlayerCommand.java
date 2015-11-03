package hu.elte.bfw1p6.poker.command.classic;

import java.math.BigDecimal;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicPlayerCommandType;

public class ClassicPlayerCommand extends PlayerCommand {

	private static final long serialVersionUID = -3424914496460268663L;

	private Card[] cards;
	/**
	 * Az utasítás típusa.
	 */
	private ClassicPlayerCommandType playerCommandType;
	
	public void setUpCallCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.CALL;
		this.callAmount = callAmount;
	}

	public void setUpBlindCommand(BigDecimal callAmount) {
		this.playerCommandType = ClassicPlayerCommandType.BLIND;
		this.callAmount = callAmount;
	}

	public void setUpCheckCommand() {
		this.playerCommandType = ClassicPlayerCommandType.CHECK;
	}

	public void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount) {
		this.playerCommandType = ClassicPlayerCommandType.RAISE;
		this.callAmount = callAmount;
		this.raiseAmount = raiseAmount;
	}

	public void setUpFoldCommand(int tempNth) {
		this.playerCommandType = ClassicPlayerCommandType.FOLD;
		this.whosQuit = tempNth;
	}

	public void setUpQuitCommand(int youAreNth) {
		this.playerCommandType = ClassicPlayerCommandType.QUIT;
		this.whosQuit = youAreNth;
	}
	
	public void setUpChangeCommand(Card[] cards) {
		this.cards = cards;
		this.playerCommandType = ClassicPlayerCommandType.CHANGE;
	}
	
	public ClassicPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
	
	public Card[] getCards() {
		return cards;
	}
}
