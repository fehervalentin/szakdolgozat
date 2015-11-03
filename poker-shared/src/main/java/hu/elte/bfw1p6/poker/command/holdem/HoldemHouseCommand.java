package hu.elte.bfw1p6.poker.command.holdem;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;


/**
 * Az osztály valósítja meg a póker szerver által küldött üzeneteket HOLDEM játék stílusban.
 * @author feher
 *
 */
public class HoldemHouseCommand extends HouseCommand {
	
	private static final long serialVersionUID = 7270842556559660805L;

	/**
	 * Az utasítás típusa.
	 */
	private HoldemHouseCommandType houseCommandType;
	
	/**
	 * Ha a szerver BLIND utasítást küld, akkor ezt a metódust kell használni.
	 * @param nthPlayer hanyadik játékos vagy a körben
	 * @param players hány játékos van összesen a körben
	 * @param dealer ki az aktuális osztó
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpBlindCommand(int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		this.houseCommandType = HoldemHouseCommandType.BLIND;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
		this.clientsNames = clientsNames;
	}
	
	/**
	 * Ha a szerver PLAYER utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpPlayerCommand(Card cards[], int whosOn) {
		this.houseCommandType = HoldemHouseCommandType.PLAYER;
		this.cards = cards;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a szerver FLOP utasítást küld, akkor ezt a metódust kell használni.
	 * @param houseCommandType FLOP
	 * @param card1 a ház első lapja, amit körbe kell küldeni
	 * @param card2 a ház második lapja, amit körbe kell küldeni
	 * @param card3 a ház harmadik lapja, amit körbe kell küldeni
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpFlopCommand(Card[] cards, int whosOn, int foldCounter) {
		this.houseCommandType = HoldemHouseCommandType.FLOP;
		this.cards = cards;
		this.whosOn = whosOn;
		this.foldCounter = foldCounter;
	}
	
	/**
	 * Ha a szerver TURN utasítást küld, akkor ezt a metódust kell használni
	 * @param card1 a háznak osztott, körbeküldendő kártyalap
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpTurnCommand(Card[] cards, int whosOn, int foldCounter) {
		this.houseCommandType = HoldemHouseCommandType.TURN;
		this.cards = cards;
		this.whosOn = whosOn;
		this.foldCounter = foldCounter;
	}
	
	/**
	 * Ha a szerver RIVER utasítást küld, akkor ezt a metódust kell használni
	 * @param card1 a háznak osztott, körbeküldendő kártyalap
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpRiverCommand(Card[] cards, int whosOn, int foldCounter) {
		this.houseCommandType = HoldemHouseCommandType.RIVER;
		this.cards = cards;
		this.whosOn = whosOn;
		this.foldCounter = foldCounter;
	}
	
	/**
	 * Ha a szerver WINNER utasítást küld, akkor ezt a metódust kell használni.
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpWinnerCommand(Card[] cards, int winner) {
		this.houseCommandType = HoldemHouseCommandType.WINNER;
		this.cards = cards;
		this.winner = winner;
	}
	
	public HoldemHouseCommandType getHouseCommandType() {
		return houseCommandType;
	}
}
