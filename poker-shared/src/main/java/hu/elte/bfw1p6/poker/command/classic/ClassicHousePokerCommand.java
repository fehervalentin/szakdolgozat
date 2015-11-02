package hu.elte.bfw1p6.poker.command.classic;

import hu.elte.bfw1p6.poker.command.HousePokerCommand;
import hu.elte.bfw1p6.poker.command.type.ClassicHousePokerCommandType;

public class ClassicHousePokerCommand extends HousePokerCommand<ClassicHousePokerCommandType> {

	private static final long serialVersionUID = 8279536095179839993L;
	
	/**
	 * Ha a classic szerver BET utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpBetCommand(int whosOn) {
		this.type = ClassicHousePokerCommandType.BET;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a classic szerver CHANGE utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpChangeCommand(int whosOn) {
		this.type = ClassicHousePokerCommandType.CHANGE;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a classic szerver BET2 utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpBet2Command(int whosOn) {
		this.type = ClassicHousePokerCommandType.BET2;
		this.whosOn = whosOn;
	}
}
