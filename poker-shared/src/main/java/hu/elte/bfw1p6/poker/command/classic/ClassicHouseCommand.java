package hu.elte.bfw1p6.poker.command.classic;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;

/**
 * A ház utasításai classic játékstílus esetén.
 * @author feher
 *
 */
public class ClassicHouseCommand extends HouseCommand {

	private static final long serialVersionUID = 1378309116747181470L;
	
	/**
	 * Az utasítás típusa.
	 */
	private ClassicHouseCommandType houseCommandType;

	@Override
	public String getCommandType() {
		return houseCommandType.name();
	}
	
	@Override
	public ClassicHouseCommand setUpBlindCommand(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		this.houseCommandType = ClassicHouseCommandType.BLIND;
		this.fixSitPosition = fixSitPosition;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
		this.clientsNames = clientsNames;
		return this;
	}
	
	@Override
	public ClassicHouseCommand setUpDealCommand(Card cards[], int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.DEAL;
		this.cards = cards;
		this.whosOn = whosOn;
		return this;
	}
	
	/**
	 * Ha a szerver CHANGE típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param whosOn az épppen következő (soron levő) játékos
	 * @return a beállítot utasítás
	 */
	public ClassicHouseCommand setUpChangeCommand(int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.CHANGE;
		this.whosOn = whosOn;
		return this;
	}
	
	@Override
	public ClassicHouseCommand setUpWinnerCommand(Card[] cards, int winner, int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.WINNER;
		this.cards = cards;
		this.winner = winner;
		this.whosOn = whosOn;
		return this;
	}
	
	/**
	 * Ha a classic szerver DEAL2 típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param cards a játékosnak küldött lapok
	 * @param whosOn az épppen következő (soron levő) játékos
	 * @return a beállítot utasítás
	 */
	public ClassicHouseCommand setUpDeal2Command(Card cards[], int whosOn) {
		this.houseCommandType = ClassicHouseCommandType.DEAL2;
		this.cards = cards;
		this.whosOn = whosOn;
		return this;
	}
	
	public ClassicHouseCommandType getHouseCommandType() {
		return houseCommandType;
	}
}