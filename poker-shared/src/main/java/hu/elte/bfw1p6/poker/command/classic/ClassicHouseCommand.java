package hu.elte.bfw1p6.poker.command.classic;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;

public class ClassicHouseCommand extends HouseCommand {

	private static final long serialVersionUID = 1378309116747181470L;
	
	/**
	 * Az utasítás típusa.
	 */
	private ClassicHouseCommandType houseCommandType;
	
	/**
	 * Ha a classic szerver BLIND utasítást küld, akkor ezt a metódust kell használni.
	 * @param nthPlayer hanyadik játékos vagy a körben
	 * @param players hány játékos van összesen a körben
	 * @param dealer ki az aktuális osztó
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpBlindCommand(int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		this.houseCommandType = ClassicHouseCommandType.BLIND;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
		this.clientsNames = clientsNames;
	}
	
	/**
	 * Ha a classic szerver DEAL utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpDealCommand(Card cards[], int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.DEAL;
		this.cards = cards;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a classic szerver CHANGE utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpChangeCommand(int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.CHANGE;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a classic szerver DEAL2 utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpDeal2Command(Card cards[], int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.DEAL2;
		this.cards = cards;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a classic szerver WINNER utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpWinnerCommand(Card[] cards, int winner) {
		this.houseCommandType = ClassicHouseCommandType.WINNER;
		this.cards = cards;
		this.winner = winner;
	}
	
	public ClassicHouseCommandType getHouseCommandType() {
		return houseCommandType;
	}

}