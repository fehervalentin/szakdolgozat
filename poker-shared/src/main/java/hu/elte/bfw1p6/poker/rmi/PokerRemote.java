package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public interface PokerRemote extends Remote{

	void deleteUser(int id) throws RemoteException;

	void modifyUser(Player player) throws RemoteException;

	void deleteTable(UUID uuid, PokerTable pokerTable) throws RemoteException, PokerDataBaseException;

	void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException;

	void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException;

	void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException;

	List<PokerTable> getTables(UUID uuid)throws RemoteException, PokerDataBaseException;

	void registerObserver(UUID uuid, RemoteObserver proc) throws RemoteException, PokerDataBaseException;

	void unRegisterObserver(UUID uuid, TableViewObserver proc) throws RemoteException;








	UUID login(String username, String password) throws RemoteException, PokerInvalidUserException;

	void logout(UUID uuid) throws RemoteException;

	boolean shutDown(UUID uuid) throws RemoteException, PokerInvalidUserException;

	boolean isAdmin(UUID uuid) throws RemoteException;

	void registration(String username, String password) throws RemoteException, PokerDataBaseException;

	void addObserver(UUID uuid, RemoteObserver observer) throws RemoteException;

	List<PokerTable> registerTableViewObserver(UUID uuid, RemoteObserver observer) throws RemoteException, PokerDataBaseException;

	void removeTableViewObserver(RemoteObserver observer) throws RemoteException;
	
	
	
	
	void sendPlayerCommand(UUID uuid, PokerTable t, PokerTableServerObserver client, PlayerCommand playerCommand) throws RemoteException;
	
	void connectToTable(UUID uuid, PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException;
}
