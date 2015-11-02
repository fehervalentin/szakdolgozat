package hu.elte.bfw1p6.poker.command.classic;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.type.ClassicPlayerPokerCommandType;

public class ClassicPlayerPokerCommand extends PlayerPokerCommand<ClassicPlayerPokerCommandType> {

	public ClassicPlayerPokerCommand(BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) {
		super(callAmount, raiseAmount, whosQuit);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 7148430682944695381L;

}
