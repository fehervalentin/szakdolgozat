package hu.elte.bfw1p6.poker.command;

import java.io.Serializable;

/**
 * A különböző utasításokat összefogó interface: server, kliens utasítás, és azokon belül is játék típus: holdem, classic
 * @author feher
 *
 */
public interface PokerCommand extends Serializable {
	int getWhosOn();
	String getCommandType();
}
