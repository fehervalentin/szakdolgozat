package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverTableViewController;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.Player;

public interface PokerRemote extends Remote{
	
	String sayHello();
	
	void deleteUser(int id) throws RemoteException;
	
	void modifyUser(Player player) throws RemoteException;
	
	void deleteTable(int id) throws RemoteException;
	
	void createTable(PTable t) throws RemoteException;
	
	void modifyTable(PTable t) throws RemoteException;
	
	void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException;
	
	List<PTable> getTables()throws RemoteException;
	
	void registerObserver(UUID uuid, PokerRemoteObserverTableViewController proc) throws RemoteException;
	
	void unRegisterObserver(UUID uuid, PokerRemoteObserverTableViewController proc) throws RemoteException;
}
