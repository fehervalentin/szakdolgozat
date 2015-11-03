package hu.elte.bfw1p6.poker.command.classic;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;

public class ClassicHouseCommand implements PokerCommand {

	private static final long serialVersionUID = 1378309116747181470L;
	
	/**
	 * Az utasítás típusa.
	 */
	private ClassicHouseCommandType houseCommandType;

	@Override
	public int getWhosOn() {
		// TODO Auto-generated method stub
		return 0;
	}

}
