package hu.elte.bfw1p6.poker.command;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.api.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public class HousePokerCommand<T extends PokerCommandType<T>> implements PokerCommand<T> {

	private static final long serialVersionUID = -7014194977573759472L;
	
	/**
	 * Az utasítás típusa.
	 */
	protected T type;

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
	
	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	public int getDealer() {
		return dealer;
	}
	
	public Card[] getCards() {
		return cards;
	}

	public void setUpDealCommand(Card[] cards, int whosOn) {
		this.cards = cards;
		this.whosOn = whosOn;
	}

	@Override
	public T getType() {
		return type;
	}
}
