package hu.elte.bfw1p6.poker.command.holdem;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHousePokerCommandType;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerPokerCommandType;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat
 * @author feher
 *
 */
public class HoldemPlayerCommand extends PlayerCommand<HoldemHousePokerCommandType> {

	public HoldemPlayerCommand(PokerCommandType<HoldemHousePokerCommandType> playerCommandType, BigDecimal callAmount,
			BigDecimal raiseAmount, Integer whosQuit) {
		super(playerCommandType, callAmount, raiseAmount, whosQuit);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -2735676865228502296L;
	
}
