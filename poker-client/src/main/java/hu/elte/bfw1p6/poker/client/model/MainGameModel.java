package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {
	
	private PokerSession pokerSession;
	private PokerRemote pokerRemote;
	
	public MainGameModel() {
		this.pokerSession = RMIRepository.getInstance().getSession();
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
	}
	
	public void connectToTable(PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException {
		pokerRemote.connectToTable(pokerSession.getId(), t, observer);
	}
	
	public void registerObserver() {
		
	}

	public void sendCommandToTable(PokerTable pokerTable, RemoteObserver observer, PlayerHoldemCommand playerHoldemCommand) throws RemoteException {
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, observer, playerHoldemCommand);
	}
}
