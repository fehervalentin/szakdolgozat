package hu.elte.bfw1p6.poker.client.controller.main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;

/**
 * A hálózati kommunikációért felelős osztály.
 * @author feher
 *
 */
public class CommunicatorController extends UnicastRemoteObject implements PokerRemoteObserver {
	
	private static final long serialVersionUID = -5481653237259385066L;
	
	/**
	 * Az értesítendő kliens oldali controller
	 */
	private PokerClientController contr;
	
	public CommunicatorController(PokerClientController contr) throws RemoteException {
		this.contr = contr;
	}

	@Override
	public void update(Object updateMsg) throws RemoteException {
		contr.update(updateMsg);
	}
}