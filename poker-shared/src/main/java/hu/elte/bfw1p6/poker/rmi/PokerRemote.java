package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.model.entity.Mode;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.Table;

public interface PokerRemote extends Remote{
	
	String sayHello();
	
	boolean deleteUser(int id) throws RemoteException;
	
	boolean modifyUser(Player player) throws RemoteException;
	
	boolean deleteTable(int id) throws RemoteException;
	
	boolean deleteMode(int id) throws RemoteException;
	
	boolean addTable(Table t) throws RemoteException;
	
	boolean addMode(Mode d) throws RemoteException;
	
	boolean modifyTable(Table t) throws RemoteException;
	
	boolean modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException;
}
