package hu.elte.bfw1p6.poker.command;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.type.api.HousePokerCommandType;

public class HousePokerCommand<T extends HousePokerCommandType<T>> extends AbstractPokerCommand<T> {

	private static final long serialVersionUID = -7014194977573759472L;

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
	 * A játékosok nicknevei.
	 */
	protected List<String> clientsNames;

	/**
	 * A nyertes játékos neve.
	 */
	protected int winner;

	/**
	 * Hány játékos dobta már el a lapjait.
	 */
	protected int foldCounter;
	
	/**
	 * Az utasítás kártyalapjai.
	 */
	protected Card[] cards;
	
	public List<String> getPlayersNames() {
		return clientsNames;
	}
	
	public int getWinner() {
		return winner;
	}
	
	public int getFoldCounter() {
		return foldCounter;
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
	
	
	public T getCommandType() {
		return type;
	}

	/**
	 * Ha a szerver BLIND utasítást küld, akkor ezt a metódust kell használni.
	 * @param nthPlayer hanyadik játékos vagy a körben
	 * @param players hány játékos van összesen a körben
	 * @param dealer ki az aktuális osztó
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpBlindCommand(int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		this.type = type.getValues()[0];
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
		this.clientsNames = clientsNames;
	}
	
	/**
	 * Ha a szerver DEAL utasítást küld, akkor ezt a metódust kell használni.
	 * @param cards a játékosnak kiszotott kártyalapok.
	 * @param whosOn az épppen következő (soron levő) játékos
	 */
	public void setUpDealCommand(Card[] cards, int whosOn) {
		this.type = type.getValues()[1];
		this.cards = cards;
		this.whosOn = whosOn;
	}
	
	/**
	 * Ha a szerver WINNER utasítást küld, akkor ezt a metódust kell használni.
	 * @param cards a nyertes kártyalapjai.
	 * @param winnerUserName a nyertes neve
	 */
	public void setUpWinnerCommand(Card[] cards, int winner) {
		this.type = type.getLastValue();
		this.cards = cards;
		this.winner = winner;
	}
}
