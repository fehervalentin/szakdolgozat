package hu.elte.bfw1p6.server;

import java.rmi.RemoteException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import hu.elte.bfw1p6.model.entity.Player;
import hu.elte.bfw1p6.rmi.PokerRemote;

public class PokerRemoteImpl implements PokerRemote {
	
	

	public PokerRemoteImpl() {
		
	}

	@Override
	public Player registration(String username, String password) throws RemoteException {
		//TODO: mehetünk persisthez
		return null;
	}

	@Override
	public boolean deleteUser(int id) {
		Subject user = SecurityUtils.getSubject();
		// TODO Auto-generated method stub
		return false;
	}
}
