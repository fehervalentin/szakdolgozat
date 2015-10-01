package hu.elte.bfw1p6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.model.LoginUser;

public interface PokerRemote extends Remote{
	public boolean login(LoginUser loginUser) throws RemoteException;
	public boolean shutDown() throws RemoteException;
}
