package hu.elte.bfw1p6.poker.client.observer.controller;

import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public interface PokerRemoteObserverTableViewController {
	public void updateTableView(List<PokerTable> tables);
}
