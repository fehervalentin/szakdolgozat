package hu.elte.bfw1p6.poker.client.controller.main;

import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

public interface PokerObserverController {
	void updateMe(Object updateMsg);
	PokerPlayer getPlayer();
}
