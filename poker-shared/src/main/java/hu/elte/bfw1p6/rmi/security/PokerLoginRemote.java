package hu.elte.bfw1p6.rmi.security;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.rmi.PokerRemote;

public interface PokerLoginRemote extends Remote {
	PokerRemote login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException;
	boolean shutDown() throws RemoteException, SecurityException, PokerInvalidUserException;
}
