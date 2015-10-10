package hu.elte.bfw1p6.poker.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverTableViewController;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.persist.pokertable.PokerTableRepository;
import hu.elte.bfw1p6.poker.model.entity.Player;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<UUID, PokerRemoteObserverTableViewController> observers;
	

	public PokerRemoteImpl() {
		observers = new HashMap<>();
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
	public void createTable(PokerTable t) throws RemoteException, SQLException {
		PokerTableRepository.getInstance().save(t);
	}

	@Override
	public void modifyTable(PokerTable t) throws RemoteException {
	}

	@Override
	public void modifyUser(Player player) throws RemoteException {
	}

	@Override
	public void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException {
	}

	@Override
	public List<PokerTable> getTables() throws RemoteException {
		return PokerTableRepository.getInstance().findAll();
	}

	@Override
	public void registerObserver(UUID uuid, PokerRemoteObserverTableViewController proc) throws RemoteException {
		observers.put(uuid, proc);
		List<PokerTable> tables = getTables();
		proc.updateTableView(tables);
	}

	@Override
	public void unRegisterObserver(UUID uuid, PokerRemoteObserverTableViewController pcc) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	
}
