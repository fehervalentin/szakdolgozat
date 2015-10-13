package hu.elte.bfw1p6.poker.client.model;

import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
//import hu.elte.bfw1p6.poker.persist.pokertable.PokerTableRepository;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {
	
	private UUID sessionId;
	private PokerRemote pokerRemote;
	private List<PokerTable> pokerTables;
	
	public MainGameModel(UUID uuid, PokerRemote pokerRemote) {
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.sessionId = RMIRepository.getInstance().getSessionId();
//		pokerTables = PokerTableRepository.getInstance().findAll();
	}
	
	public void registerObserver() {
		
	}
}
