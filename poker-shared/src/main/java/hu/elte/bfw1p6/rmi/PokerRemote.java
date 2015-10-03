package hu.elte.bfw1p6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.model.LoginUser;
import hu.elte.bfw1p6.model.entity.Player;

public interface PokerRemote extends Remote{
	
	String sayHello();
	
	Player registration(LoginUser loginUser) throws RemoteException;

	boolean deleteUser(int id);
}
