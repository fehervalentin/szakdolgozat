package hu.elte.bfw1p6.poker.command;

import java.io.Serializable;

/**
 * A különböző utasításokat összefogó interface:
 * server, kliens utasítás, és azokon belül is játék típus: holdem, classic
 * @author feher
 *
 */
public interface PokerCommand extends Serializable {
	
	/**
	 * Az utasítástól lekérdezhető, hogy ki következik.
	 * @return a soron következő játékos sorszáma
	 */
	int getWhosOn();
	
	/**
	 * Az utasítás típusa lekérdezhető.
	 * @return az utasítás típusa
	 */
	String getCommandType();
}