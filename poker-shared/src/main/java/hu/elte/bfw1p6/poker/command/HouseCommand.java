package hu.elte.bfw1p6.poker.command;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

public class HouseCommand implements PokerCommand {

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
	 * Az utasítás első lapja.
	 */
	protected Card card1;
	
	/**
	 * Az utasítás második lapja.
	 */
	protected Card card2;
	
	/**
	 * Az utasítás harmadik lapja.
	 */
	protected Card card3;
	
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
