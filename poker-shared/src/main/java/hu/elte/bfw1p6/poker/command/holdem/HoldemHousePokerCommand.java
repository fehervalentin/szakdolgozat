package hu.elte.bfw1p6.poker.command.holdem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HousePokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHousePokerCommandType;


/**
 * Az osztály valósítja meg a póker szerver által küldött üzeneteket HOLDEM játék stílusban.
 * @author feher
 *
 */
public class HoldemHousePokerCommand extends HousePokerCommand<HoldemHousePokerCommandType> {
	
	private static final long serialVersionUID = 7270842556559660805L;
	
	private static HashSet<HoldemHousePokerCommandType> acceptedTypes;
	
	
	public HoldemHousePokerCommand() {
		super();
		acceptedTypes = new HashSet<>();
		acceptedTypes.add(HoldemHousePokerCommandType.FLOP);
		acceptedTypes.add(HoldemHousePokerCommandType.TURN);
		acceptedTypes.add(HoldemHousePokerCommandType.RIVER);
	}



	/**
	 * Ha a holdem szerver FLOP vagy TURN vagy RIVER utasítást küld, akkor ezt a metódust kell használni.
	 * @param houseCommandType FLOP vagy TURN vagy RIVER
	 * @param cards a ház lapjai
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpFlopTurnRiverCommand(HoldemHousePokerCommandType type, Card[] cards, int whosOn, int foldCounter) {
		if (!acceptedTypes.contains(type)) {
			throw new IllegalArgumentException("Hibás utasítás típus! A megengedett utasítás típusok: " + acceptedTypes.toArray().toString());
		}
		this.type = type;
		this.cards = cards;
		this.whosOn = whosOn;
		this.foldCounter = foldCounter;
	}
}
