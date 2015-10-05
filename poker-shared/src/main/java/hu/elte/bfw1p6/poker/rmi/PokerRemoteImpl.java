package hu.elte.bfw1p6.poker.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.persist.dao.PTableDAO;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private transient PTableDAO tableDAO;

	public PokerRemoteImpl() {
		tableDAO = new PTableDAO();
	}

	@Override
	public void deleteUser(int id) {
	}

	@Override
	public String sayHello() {
		return "hello";
	}

	@Override
	public void deleteTable(int id) throws RemoteException {
	}

	@Override
	public void createTable(PTable t) throws RemoteException {
//		 .persistTable(t);
		(new PTableDAO()).persistTable(t);
	}

	@Override
	public void modifyTable(PTable t) throws RemoteException {
	}

	@Override
	public void modifyUser(Player player) throws RemoteException {
	}

	@Override
	public void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException {
	}

	@Override
	public List<PTable> getTables() throws RemoteException {
		return null;
//		return tableDAO.getTables();
	}
	
	
}
