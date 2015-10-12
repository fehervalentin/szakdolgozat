package hu.elte.bfw1p6.poker.client.controller;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.observer.nemtudom.RemoteObserver;

public class CommunicationController implements RemoteObserver {

	@Override
	public void update(Object observable, Object updateMsg) throws RemoteException {
		System.out.println("szerver hivta meg a jo kurva anyadat!");
	}

}
