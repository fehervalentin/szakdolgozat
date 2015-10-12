package hu.elte.bfw1p6.poker.client.observer.controller;

import java.io.Serializable;
import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class TableViewObserverImpl implements TableViewObserver, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Kutyafasz kutyaFasz;
	
	public TableViewObserverImpl(Kutyafasz kutyaFasz) {
		this.kutyaFasz = kutyaFasz;
	}

	@Override
	public void updateTableView(List<PokerTable> tables) {
		this.kutyaFasz.updateKURVAANYAD();
		System.out.println("szerver hivta!");
	}

}
