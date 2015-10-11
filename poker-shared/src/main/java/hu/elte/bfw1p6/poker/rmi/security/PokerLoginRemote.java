package hu.elte.bfw1p6.poker.rmi.security;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.UUID;

import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;


public interface PokerLoginRemote extends Remote {
	
	PokerRemote getPokerRemote(UUID uuid) throws RemoteException, PokerInvalidUserException;
	
	UUID login(String username, String password) throws RemoteException, PokerInvalidUserException;
	
	void logout(UUID uuid) throws RemoteException;
	
	boolean shutDown(UUID uuid) throws RemoteException, PokerInvalidUserException;
	
	boolean isAdmin(UUID uuid) throws RemoteException;
	
	void registration(String username, String password) throws RemoteException, SQLException;
}
