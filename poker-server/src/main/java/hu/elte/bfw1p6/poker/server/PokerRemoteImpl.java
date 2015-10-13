package hu.elte.bfw1p6.poker.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableListerObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.database.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.SQLExceptionInterceptor;
import hu.elte.bfw1p6.poker.persist.pokertable.PokerTableRepository;
import hu.elte.bfw1p6.poker.persist.user.UserBuilder;
import hu.elte.bfw1p6.poker.persist.user.UserRepository;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.security.service.SessionService;

public class PokerRemoteImpl extends Observable implements PokerRemote, Serializable {

	private static final long serialVersionUID = 1L;

	private PokerProperties pokerProperties;

	private SessionService sessionService;
	
	private List<TableListerObserver> tlos;

	public PokerRemoteImpl() throws RemoteException {
		this.pokerProperties = PokerProperties.getInstance();
		this.sessionService = new SessionService();
		tlos = new ArrayList<>();

		try {
			System.out.println("***POKER SZERVER***");
			//PokerLoginRemote pokerLoginRemote = (PokerLoginRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("port")));
			System.out.println(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			System.out.println(pokerProperties.getProperty("name"));
			/*Registry registry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			try {
				registry.bind(pokerProperties.getProperty("name"), this);
				System.out.println("bindelte");
			} catch (AlreadyBoundException e) {
				 e.printStackTrace();
			}*/
			Registry rmiRegistry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			PokerRemote pokerRemote = (PokerRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("rmiport")));
			rmiRegistry.bind(pokerProperties.getProperty("name"), pokerRemote);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		System.out.println("A szerver elindult");
	}

	@Override
	public synchronized void deleteUser(int id) {
	}

	@Override
	public synchronized void deleteTable(int id) throws RemoteException {
	}

	@Override
	public synchronized void createTable(PokerTable t) throws RemoteException, SQLException {
		PokerTableRepository.getInstance().save(t);
		this.notifyObservers();
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
	public synchronized void registerObserver(UUID uuid, RemoteObserver observer) throws RemoteException {
		//		System.out.println(uuid.toString());
		//		observers.put(uuid, proc);
		//		System.out.println(observers.hashCode());
		//		System.out.println(this);
		//		System.out.println("Observerek: " + observers.size());
		List<PokerTable> tables = getTables();
		TableListerObserver tlo = new TableListerObserver(observer);
		this.addObserver(tlo);
		String asd = "muha csaba222222!";
		this.setChanged();
		this.notifyObservers(asd);
		//		proc.updateTableView(tables);

	}

	@Override
	public void unRegisterObserver(UUID uuid, TableViewObserver pcc) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public UUID login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException {
		return sessionService.authenticate(username, password);
	}

	@Override
	public boolean shutDown(UUID uuid) throws RemoteException {
		try {
			UnicastRemoteObject.unexportObject(this, true);
			System.out.println("A szerver leállt");
			return true;
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void logout(UUID uuid) throws RemoteException {
		sessionService.invalidate(uuid);
	}

	@Override
	public boolean isAdmin(UUID uuid) throws RemoteException {
		// TODO DAO-tól elkérni servicen keresztül?
		return false;
	}

	@Override
	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		User u = UserBuilder.geInstance().buildUser(username, password);
		try {
		UserRepository.save(u);
		} catch(SQLException ex) {
			throw SQLExceptionInterceptor.getInstance().interceptException(ex);
		}
	}

	@Override
	public void addObserver(RemoteObserver o) throws RemoteException {
		TableListerObserver wp = new TableListerObserver(o);
		tlos.add(wp);
		this.addObserver(wp);
		this.setChanged();
		String asd = "muha!";
		this.notifyObservers(asd);
	}

	@Override
	public List<PokerTable> registerTableViewObserver(RemoteObserver observer) throws RemoteException {
		TableListerObserver tvo = new TableListerObserver(observer);
		this.addObserver(tvo);
		this.setChanged();
		this.notifyObservers(getTables());
		return getTables();
	}
	
	private TableListerObserver removeTVO(RemoteObserver observer) {
		for (TableListerObserver tableListerObserver : tlos) {
			if (tableListerObserver.getRo().equals(observer)) {
				return tableListerObserver;
			}
		}
		return null;
	}

	@Override
	public void removeTableViewObserver(RemoteObserver observer) {
		//TableListerObserver wp = tlos.get(tlos.indexOf((TableListerObserver)observer));
		TableListerObserver tlo = removeTVO(observer);
		tlos.remove(tlo);
		this.deleteObserver(tlo); //TODO NEM BIZTOS...
	}

}
