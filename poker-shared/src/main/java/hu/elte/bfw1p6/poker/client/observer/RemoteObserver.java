package hu.elte.bfw1p6.poker.client.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

/**
 * A kliens implementálja.
 * @author feher
 *
 */
public interface RemoteObserver extends Remote {

    void update(Object updateMsg) throws RemoteException;
    
    PokerPlayer getPlayer() throws RemoteException;

}
