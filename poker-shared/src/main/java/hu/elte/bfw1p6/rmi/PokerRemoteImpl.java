package hu.elte.bfw1p6.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.model.LoginUser;
import hu.elte.bfw1p6.model.entity.Player;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	

	public PokerRemoteImpl() {
	}

	@Override
	public Player registration(LoginUser loginUser) throws RemoteException {
		//TODO: mehetünk persisthez
		return null;
	}

	@Override
	public boolean deleteUser(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String sayHello() {
		return "hello";
	}
}
