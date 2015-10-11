package hu.elte.bfw1p6.poker.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverGameController;
import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverTableViewController;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.persist.pokertable.PokerTableRepository;

public class PokerRemoteImpl implements PokerRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//private HashTable<UUID, PokerRemoteObserverTableViewController> observers;
	
	private Hashtable<UUID, PokerRemoteObserverTableViewController> observers;
	
	private int myCounter = 0;
	
	private AtomicInteger asd;

	public PokerRemoteImpl() {
		asd = new AtomicInteger(0);
		observers = new Hashtable<>();
		
		
	}

	@Override
	public synchronized void deleteUser(int id) {
	}

	@Override
	public String sayHello() {
		return "hello";
	}

	@Override
	public synchronized void deleteTable(int id) throws RemoteException {
	}

	@Override
	public synchronized void createTable(PokerTable t) throws RemoteException, SQLException {
		PokerTableRepository.getInstance().save(t);
		notifyAllObserver();
	}

	@Override
	public synchronized void modifyTable(PokerTable t) throws RemoteException {
	}

	@Override
	public synchronized void modifyUser(Player player) throws RemoteException {
	}

	@Override
	public synchronized void modifyPassword(String username, String oldPassword, String newPassword) throws RemoteException {
	}

	@Override
	public List<PokerTable> getTables() throws RemoteException {
		return PokerTableRepository.getInstance().findAll();
	}

	@Override
	public synchronized void registerObserver(UUID uuid, PokerRemoteObserverTableViewController proc) throws RemoteException {
		System.out.println(uuid.toString());
		myCounter++;
		System.out.println("Atomic :" + asd.incrementAndGet());
		System.out.println("regisztrálom az observert");
		observers.put(uuid, proc);
		List<PokerTable> tables = getTables();
		proc.updateTableView(tables);
	}

	@Override
	public void unRegisterObserver(UUID uuid, PokerRemoteObserverTableViewController pcc) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disConnectFromTable(PokerTable pokerTable, UUID uuid, PokerRemoteObserverGameController pmgc) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectToTable(PokerTable pokerTable, PokerRemoteObserverGameController progc, UUID uuid)
			throws RemoteException {
		progc.sayHello();
		// TODO KURVAFONTOS
		
	}
	
	private void notifyAllObserver() throws RemoteException {
		System.out.println("My counter: " + myCounter);
		Iterator it = observers.entrySet().iterator();
		System.out.println("meret: " + observers.size());
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        ((PokerRemoteObserverTableViewController)pair.getValue()).updateTableView(getTables());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
}
