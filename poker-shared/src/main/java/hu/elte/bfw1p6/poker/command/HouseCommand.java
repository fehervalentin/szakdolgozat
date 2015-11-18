package hu.elte.bfw1p6.poker.command;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

/**
 * Olyan utasítások, amelyeket a kommunikáció során a szerverek (házak) küldenek.
 * @author feher
 *
 */
public abstract class HouseCommand implements PokerCommand {

	private static final long serialVersionUID = 1571742099156009456L;
	
	/**
	 * Az utasítás lapjai.
	 */
	protected Card cards[];

	/**
	 * Hanyadik játékos vagy az asztalnál.
	 */
	protected int nthPlayer;
	
	/**
	 * Hány játékos van összesen.
	 */
	protected int players;
	
	/**
	 * Ki az osztó az adott leosztásban.
	 */
	protected int dealer;
	
	/**
	 * Ki következik éppen.
	 */
	protected int whosOn;
	
	/**
	 * A játékosok nicknevei.
	 */
	protected List<String> clientsNames;

	/**
	 * A nyertes játékos sorszáma.
	 */
	protected int winner;

	/**
	 * A fixen elfoglalt ülőhely az asztalnál.
	 */
	protected int fixSitPosition;
	
	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	/**
	 * Ha a szerver BLIND típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param nthPlayer hanyadik játékos vagy a körben
	 * @param players hány játékos van összesen a körben
	 * @param dealer ki az aktuális osztó
	 * @param whosOn az épppen következő (soron levő) játékos
	 * @return a beállítot utasítás
	 */
	public abstract HouseCommand setUpBlindCommand(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames);
	
	/**
	 * Ha a szerver DEAL típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param cards a játékosnak küldött lapok
	 * @param whosOn az épppen következő (soron levő) játékos
	 * @return a beállítot utasítás
	 */
	public abstract HouseCommand setUpDealCommand(Card cards[], int whosOn);
	
	/**
	 * Ha a szerver WINNER típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param cards a nyertes kártyalapok
	 * @param winnerUserName a nyertes neve
	 * @return a beállított utasítás
	 */
	public abstract HouseCommand setUpWinnerCommand(Card[] cards, int winner, int whosOn);
	
	public abstract String getCommandType();
	
	public List<String> getPlayersNames() {
		return clientsNames;
	}
	
	public int getWinner() {
		return winner;
	}
	
	public int getNthPlayer() {
		return nthPlayer;
	}
	public int getPlayers() {
		return players;
	}
	
	public int getDealer() {
		return dealer;
	}
	
	public Card[] getCards() {
		return cards;
	}

	public int getFixSitPosition() {
		return fixSitPosition;
	}
}