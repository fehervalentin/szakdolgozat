package hu.elte.bfw1p6.rmi.security;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import hu.elte.bfw1p6.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.rmi.PokerRemote;

public interface PokerLoginRemote extends Remote {
	
	PokerRemote getPokerRemote(UUID uuid) throws RemoteException, SecurityException, PokerInvalidUserException;
	
	/**
	 * 
	 * @return UUID id
	 * @throws RemoteException
	 * @throws SecurityException
	 * @throws PokerInvalidUserException
	 */
	UUID login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException;
	
	void logout(UUID uuid) throws RemoteException;
	
	boolean shutDown() throws RemoteException, SecurityException, PokerInvalidUserException;
}
