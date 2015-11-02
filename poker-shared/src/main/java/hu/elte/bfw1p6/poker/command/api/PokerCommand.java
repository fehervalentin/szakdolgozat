package hu.elte.bfw1p6.poker.command.api;

import java.io.Serializable;

import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

/**
 * A különböző utasításokat összefogó interface: server, kliens utasítás, és azokon belül is játék típus: holdem, classic
 * @author feher
 *
 */
public interface PokerCommand<T extends PokerCommandType<T>> extends Serializable {
	int getWhosOn();
	T getType();
}
