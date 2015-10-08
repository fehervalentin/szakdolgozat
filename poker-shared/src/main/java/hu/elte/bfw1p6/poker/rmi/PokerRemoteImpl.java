package hu.elte.bfw1p6.poker.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverController;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.persist.ptable.PTableRepository;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<UUID, PokerRemoteObserverController> observers;
	

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
	public void createTable(PTable t) throws RemoteException {
		PTableRepository.save(t);
		System.out.println("létrehozta");
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

	@Override
	public void registerObserver(UUID uuid, PokerRemoteObserverController proc) throws RemoteException {
		observers.put(uuid, proc);
		proc.notifyPokerClientController();
	}

	@Override
	public void unRegisterObserver(UUID uuid, PokerRemoteObserverController pcc) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	
}
