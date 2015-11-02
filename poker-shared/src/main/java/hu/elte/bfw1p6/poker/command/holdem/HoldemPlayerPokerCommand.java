package hu.elte.bfw1p6.poker.command.holdem;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerPokerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat.
 * @author feher
 *
 */
public class HoldemPlayerPokerCommand extends PlayerPokerCommand<HoldemPlayerPokerCommandType> {
	
	private static final long serialVersionUID = -2735676865228502296L;

	public HoldemPlayerPokerCommand(BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
		super(callAmount, raiseAmount, whosQuit);
	}
}
