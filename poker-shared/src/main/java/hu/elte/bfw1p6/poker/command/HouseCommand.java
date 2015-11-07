package hu.elte.bfw1p6.poker.command;

import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

public abstract class HouseCommand implements PokerCommand {

	private static final long serialVersionUID = 1571742099156009456L;
	
	/**
	 * Az utasítás első lapja.
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
	 * Hányan dobták be a lapjaikat.
	 */
	protected int foldCounter;

	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	public abstract String getCommandType();
	
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
}
