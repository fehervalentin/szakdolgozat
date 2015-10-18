package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {
	
	private PokerSession pokerSession;
	private PokerRemote pokerRemote;
	
	public MainGameModel() {
		this.pokerSession = Model.getInstance().getPokerSession();
		
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
	}
	
	public void connectToTable(PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		pokerRemote.connectToTable(pokerSession.getId(), t, observer);
	}
	
	public void registerObserver() {
		
	}

	public void sendCommandToTable(PokerTable pokerTable, RemoteObserver observer, PlayerHoldemCommand playerHoldemCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		playerHoldemCommand.setSender(pokerSession.getPlayer().getUserName());
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, observer, playerHoldemCommand);
		pokerSession.setPlayer(pokerRemote.refreshPlayer(pokerSession.getId()));
		System.out.println("uj balance: " + pokerSession.getPlayer().getBalance());
	}

//	public void decreaseBalance(PokerTable pokerTable, Remote observer BigDecimal amount) {
//		pokerRemote.sendPlayerCommand(pokerSession.getId(), pok, client, playerCommand);
//		pokerSession.setPlayer(player);
//	}
	
	public String getUserName() {
		return pokerSession.getPlayer().getUserName();
	}
}
