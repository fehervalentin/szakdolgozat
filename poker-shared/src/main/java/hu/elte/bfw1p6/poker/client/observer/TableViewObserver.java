package hu.elte.bfw1p6.poker.client.observer;

import java.rmi.Remote;
import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public interface TableViewObserver extends Remote {
	public void updateTableView(List<PokerTable> tables);
}
