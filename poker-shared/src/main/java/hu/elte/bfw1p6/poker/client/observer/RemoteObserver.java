package hu.elte.bfw1p6.poker.client.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * A kliens implementálja
 * @author feher
 *
 */
public interface RemoteObserver extends Remote {

    void update(Object updateMsg) throws RemoteException;

}
