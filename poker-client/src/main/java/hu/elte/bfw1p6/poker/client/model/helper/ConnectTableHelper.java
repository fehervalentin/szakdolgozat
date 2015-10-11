package hu.elte.bfw1p6.poker.client.model.helper;

import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ConnectTableHelper {
	private static ConnectTableHelper instance = null;
	private PokerTable pokerTable;
	
	private ConnectTableHelper() {
	}
	
	public static ConnectTableHelper getInstance() {
		if(instance == null) {
			instance = new ConnectTableHelper();
		}
		return instance;
	}
	
	public void setPokerTable(PokerTable t) {
		this.pokerTable = t;
	}
	
	public PokerTable getPokerTable() {
		return pokerTable;
	}
	
}
