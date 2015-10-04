package hu.elte.bfw1p6.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.model.entity.Mode;
import hu.elte.bfw1p6.model.entity.Player;
import hu.elte.bfw1p6.model.entity.Table;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	

	public PokerRemoteImpl() {
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

	@Override
	public boolean deleteTable(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteMode(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addTable(Table t) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addMode(Mode d) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyTable(Table t) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyUser(Player player) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
}
