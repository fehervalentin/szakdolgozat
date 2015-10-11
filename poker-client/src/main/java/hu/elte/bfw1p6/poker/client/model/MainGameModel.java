package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverGameController;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.persist.pokertable.PokerTableRepository;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {
	
	private UUID sessionId;
	private PokerRemote pokerRemote;
	private List<PokerTable> pokerTables;
	
	public MainGameModel(UUID uuid, PokerRemote pokerRemote) {
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.sessionId = RMIRepository.getInstance().getSessionId();
		pokerTables = PokerTableRepository.getInstance().findAll();
	}
	
	public void registerObserver() {
		
	}

	public void connect(PokerTable pokerTable, PokerRemoteObserverGameController progc) {
		try {
			pokerRemote.connectToTable(pokerTable, progc, sessionId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
