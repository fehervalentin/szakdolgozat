package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.TableViewObserver;
import hu.elte.bfw1p6.poker.client.observer.nemtudom.RemoteObserver;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.Player;

public interface PokerRemote extends Remote{
	
	//String sayHello();
	
	void deleteUser(int id) throws RemoteException;
	
	void modifyUser(Player player) throws RemoteException;
	
	void deleteTable(int id) throws RemoteException;
	
	void createTable(PokerTable t) throws RemoteException, SQLException;
	
	void modifyTable(PokerTable t) throws RemoteException;
	
	void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException;
	
	List<PokerTable> getTables()throws RemoteException;
	
	void registerObserver(UUID uuid, RemoteObserver proc) throws RemoteException;
	
	void unRegisterObserver(UUID uuid, TableViewObserver proc) throws RemoteException;
	
	
	
	
	
	
	
	UUID login(String username, String password) throws RemoteException, PokerInvalidUserException;
	
	void logout(UUID uuid) throws RemoteException;
	
	boolean shutDown(UUID uuid) throws RemoteException, PokerInvalidUserException;
	
	boolean isAdmin(UUID uuid) throws RemoteException;
	
	void registration(String username, String password) throws RemoteException, SQLException;
	
	void addObserver(RemoteObserver observer) throws RemoteException;
	
	List<PokerTable> registerTableViewObserver(RemoteObserver observer) throws RemoteException;

	void removeTableViewObserver(RemoteObserver observer) throws RemoteException;
}
