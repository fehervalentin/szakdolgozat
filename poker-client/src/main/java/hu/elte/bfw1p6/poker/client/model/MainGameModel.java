package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {
	
	private UUID sessionId;
	private PokerRemote pokerRemote;
	
	public MainGameModel(UUID uuid, PokerRemote pokerRemote) {
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.sessionId = RMIRepository.getInstance().getSessionId();
	}
	
	public void connectToTable(PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException {
		pokerRemote.connectToTable(sessionId, t, observer);
	}
	
	public void registerObserver() {
		
	}
}
