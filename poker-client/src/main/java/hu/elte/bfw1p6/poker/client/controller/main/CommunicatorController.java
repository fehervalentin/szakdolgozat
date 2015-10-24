package hu.elte.bfw1p6.poker.client.controller.main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

public class CommunicatorController extends UnicastRemoteObject implements RemoteObserver {
	
	private static final long serialVersionUID = 1L;
	
	private PokerObserverController contr;
	
	public CommunicatorController(PokerObserverController contr) throws RemoteException {
		this.contr = contr;
	}

	@Override
	public void update(Object updateMsg) throws RemoteException {
		contr.updateMe(updateMsg);
	}
}