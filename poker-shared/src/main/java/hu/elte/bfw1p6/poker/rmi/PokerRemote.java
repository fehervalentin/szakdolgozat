package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverController;
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
	
	void registerObserver(UUID uuid, PokerRemoteObserverController proc) throws RemoteException;
	
	void unRegisterObserver(UUID uuid, PokerRemoteObserverController proc) throws RemoteException;
}
