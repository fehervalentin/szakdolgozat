package hu.elte.bfw1p6.poker.client.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A kliens az observer szerepét tölti be a kommunikációban. A szerver az observereket tudja "frissíteni",
 * vagyis utasításokat tud nekik küldeni.
 * @author feher
 *
 */
public interface PokerRemoteObserver extends Remote {

	/**
	 * A szerver ezen metódus hívásán keresztül tud a kliensekkel kommunikálni.
	 * @param updateMsg az üzenet
	 * @throws RemoteException
	 */
    void update(Object updateMsg) throws RemoteException;
}
