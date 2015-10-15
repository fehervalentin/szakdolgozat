package hu.elte.bfw1p6.poker.client.observer;

import hu.elte.bfw1p6.poker.command.PokerCommand;

public interface PokerTableServerObserver {
	void updateClient(PokerCommand houseCommand);
}
