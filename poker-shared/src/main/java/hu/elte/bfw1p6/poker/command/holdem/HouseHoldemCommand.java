package hu.elte.bfw1p6.poker.command.holdem;

import java.io.Serializable;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;


/**
 * Az osztály valósítja meg a póker szerver által küldött üzeneteket HOLDEM játék stílusban.
 * @author feher
 *
 */
public class HouseHoldemCommand implements PokerCommand, Serializable {
	
	private static final long serialVersionUID = 7270842556559660805L;

	/**
	 * Hanyadik játékos vagy az asztalnál.
	 */
	private int nthPlayer;
	
	/**
	 * Hány játékos van összesen.
	 */
	private int players;
	
	/**
	 * Ki az osztó az adott leosztásban.
	 */
	private int dealer;
	
	/**
	 * Ki következik éppen.
	 */
	private int whosOn;
	
	private List<String> playersNames;
	
	private HoldemHouseCommandType houseCommandType;
	private Card card1, card2, card3;

	private String winnerUserName;
	
	/**
	 * Ha a szerver bekéri a vakokat, akkor ezt a konstruktort kell használni.
	 * @param houseCommandType BLIND
	 * @param nthPlayer hanyadik játékos vagy a körben
	 * @param players hány játékos van összesen a körben
	 * @param dealer ki az aktuális osztó
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, int nthPlayer, int players, int dealer, int whosOn) {
		this.houseCommandType = houseCommandType;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
	}
	
	public void setUpBlindCommand(int nthPlayer, int players, int dealer, int whosOn) {
		this.houseCommandType = HoldemHouseCommandType.BLIND;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
	}

	/**
	 * Ha a szerver TURN vagy RIVER leosztást küld, akkor ezt a konstruktort kell használni
	 * @param houseCommandType TURN vagy RIVER
	 * @param card1 a háznak osztott, körbeküldendő kártyalap
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, int whosOn) {
		this.houseCommandType = houseCommandType;
		this.card1 = card1;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a szerver PLAYER leosztást küld, akkor ezt a konstruktort kell használni
	 * @param houseCommandType PLAYER
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, Card card2, int whosOn) {
		this(houseCommandType, card1, whosOn);
		this.card2 = card2;
		
	}
	
	/**
	 * Ha a szerver FLOP leosztást küld, akkor ezt a konstruktort kell használni.
	 * @param houseCommandType FLOP
	 * @param card1 a ház első lapja, amit körbe kell küldeni
	 * @param card2 a ház második lapja, amit körbe kell küldeni
	 * @param card3 a ház harmadik lapja, amit körbe kell küldeni
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, Card card2, Card card3, int whosOn) {
		this(houseCommandType, card1, card2, whosOn);
		this.card3 = card3;
	}
	
	/**
	 * Ha a szerver WINNER utasítást küld, akkor ezt a konstruktort kell használni.
	 * @param houseCommandType WINNER
	 * @param card1 a nyertes első lapja
	 * @param card2 a nyertes második lapja
	 * @param winnerUserName a nyertes neve
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, Card card2, String winnerUserName) {
		this(houseCommandType, card1, card2, -1);
		this.winnerUserName = winnerUserName;
	}
	
	public HoldemHouseCommandType getHouseCommandType() {
		return houseCommandType;
	}
	
	public Card getCard1() {
		return card1;
	}
	
	public Card getCard2() {
		return card2;
	}
	
	public Card getCard3() {
		return card3;
	}
	
	public int getNthPlayer() {
		return nthPlayer;
	}
	public int getPlayers() {
		return players;
	}
	
	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	public int getDealer() {
		return dealer;
	}

	@Override
	public String toString() {
		return "[" + houseCommandType + " " + card1 + " " + card2 + " " + card3 + "]";
	}

	public void setPlayersNames(List<String> playersNames) {
		this.playersNames = playersNames;
	}
	
	public List<String> getPlayersNames() {
		return playersNames;
	}
	
	public String getWinnerUserName() {
		return winnerUserName;
	}
}
