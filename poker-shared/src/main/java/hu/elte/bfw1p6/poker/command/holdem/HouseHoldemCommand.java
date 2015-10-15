package hu.elte.bfw1p6.poker.command.holdem;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.model.Card;


/**
 * Az osztály valósítja meg a póker szerver által küldött üzeneteket HOLDEM játék stílusban.
 * @author feher
 *
 */
public class HouseHoldemCommand implements PokerCommand {
	private HoldemHouseCommandType houseCommandType;
	private Card card1, card2, card3;

	/**
	 * Ha a szerver TURN vagy RIVER leosztást küld, akkor ezt a konstruktort kell használni
	 * @param houseCommandType TURN vagy RIVER
	 * @param card1 a háznak osztott, körbeküldendő kártyalap
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1) {
		this.houseCommandType = houseCommandType;
	}
	
	/**
	 * Ha a szerver PLAYER leosztást küld, akkor ezt a konstruktort kell használni
	 * @param houseCommandType PLAYER
	 * @param card1 a playernek küldött első kártya
	 * @param card2 a playernek küldött második kártya
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, Card card2) {
		this(houseCommandType, card1);
		this.card2 = card2;
	}
	
	/**
	 * Ha a szerver FLOP leosztást küld, akkor ezt a konstruktort kell használni
	 * @param houseCommandType FLOP
	 * @param card1 a ház első lapja, amit körbe kell küldeni
	 * @param card2 a ház második lapja, amit körbe kell küldeni
	 * @param card3 a ház harmadik lapja, amit körbe kell küldeni
	 */
	public HouseHoldemCommand(HoldemHouseCommandType houseCommandType, Card card1, Card card2, Card card3) {
		this(houseCommandType, card1);
		this.card2 = card2;
		this.card3 = card3;
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
}
