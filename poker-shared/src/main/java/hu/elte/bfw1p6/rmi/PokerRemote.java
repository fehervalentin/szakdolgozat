package hu.elte.bfw1p6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.shiro.authz.annotation.RequiresRoles;

import hu.elte.bfw1p6.model.entity.Player;

public interface PokerRemote extends Remote{
	Player registration(String username, String password) throws RemoteException;

	@RequiresRoles("administrator")
	boolean deleteUser(int id);
}
