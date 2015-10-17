package hu.elte.bfw1p6.poker.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableListerObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.PokerTableRepository;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.persist.user.UserBuilder;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.security.service.SessionService;

public class PokerRemoteImpl extends Observable implements PokerRemote, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String UNAUTH_ERR_MSG = "Nem vagy autentikálva!";

	private PokerProperties pokerProperties;

	private SessionService sessionService;

	private List<TableListerObserver> tlos;

	private Hashtable<String, HoldemPokerTableServer> pokerTableservers;

	public PokerRemoteImpl() throws RemoteException {
		this.pokerProperties = PokerProperties.getInstance();
		this.sessionService = new SessionService();
		tlos = new ArrayList<>();
		pokerTableservers = new Hashtable<>();
		List<PokerTable> tables = null;
		try {
			tables = PokerTableRepository.getInstance().findAll();
		} catch (PokerDataBaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pokerTableservers.put(tables.get(0).getName(), new HoldemPokerTableServer(tables.get(0)));

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
	public synchronized void deleteTable(UUID uuid, PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		PokerTableRepository.getInstance().deleteTable(pokerTable);
		this.notifyObservers();
	}

	@Override
	public synchronized void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			PokerTableRepository.getInstance().save(t);
			this.notifyObservers();
		} else {
			throw new PokerUnauthenticatedException(UNAUTH_ERR_MSG);
		}
	}

	@Override
	public synchronized void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException {
		PokerTableRepository.getInstance().modify(t);
		this.notifyObservers();
	}

	@Override
	public synchronized void modifyPassword(UUID uuid, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			String userName = sessionService.lookUpUsername(uuid);
			User u = UserRepository.getInstance().findByUserName(userName);
			if (BCrypt.checkpw(oldPassword, u.getPassword())) {
				newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
				UserRepository.getInstance().modifyPassword(userName, newPassword);
			} else {
				throw new PokerInvalidPassword("Hibás jelszó!");
			}
		} else {
			throw new PokerUnauthenticatedException(UNAUTH_ERR_MSG);
		}
	}

	@Override
	public List<PokerTable> getTables(UUID uuid) throws RemoteException, PokerDataBaseException {
		return PokerTableRepository.getInstance().findAll();
	}

	@Override
	public synchronized void registerObserver(UUID uuid, RemoteObserver observer) throws RemoteException, PokerDataBaseException {
		//		System.out.println(uuid.toString());
		//		observers.put(uuid, proc);
		//		System.out.println(observers.hashCode());
		//		System.out.println(this);
		//		System.out.println("Observerek: " + observers.size());
		List<PokerTable> tables = getTables(uuid);
		TableListerObserver tlo = new TableListerObserver(observer);
		this.addObserver(tlo);
		String asd = "muha222!";
		this.setChanged();
		this.notifyObservers(asd);
		//		proc.updateTableView(tables);

	}

	@Override
	public void unRegisterObserver(UUID uuid, TableViewObserver pcc) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public PokerSession login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException, PokerDataBaseException {
		PokerSession pokerSession = sessionService.authenticate(username, password);
		return pokerSession;
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
		UserRepository.getInstance().save(u);
	}

	@Override
	public void addObserver(UUID uuid, RemoteObserver o) throws RemoteException {
		TableListerObserver wp = new TableListerObserver(o);
		tlos.add(wp);
		this.addObserver(wp);
		this.setChanged();
		String asd = "muha!";
		this.notifyObservers(asd);
	}

	@Override
	public List<PokerTable> registerTableViewObserver(UUID uuid, RemoteObserver observer) throws RemoteException, PokerDataBaseException {
		TableListerObserver tvo = new TableListerObserver(observer);
		this.addObserver(tvo);
		this.setChanged();
		this.notifyObservers(getTables(uuid));
		return getTables(uuid);
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

	@Override
	public void sendPlayerCommand(UUID uuid, PokerTable t, RemoteObserver client, PlayerHoldemCommand playerCommand) throws RemoteException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableservers.get(t.getName()).receivePlayerCommand(client, playerCommand);
		}

	}

	@Override
	public void connectToTable(UUID uuid, PokerTable t, RemoteObserver client) throws RemoteException, PokerTooMuchPlayerException {
		if (sessionService.isAuthenticated(uuid)) {
			HoldemPokerTableServer pts = pokerTableservers.get(t.getName());
			pts.join(client);
		}
	}

	@Override
	public void deletePlayer(UUID uuid, Player player) throws RemoteException, PokerDataBaseException {
		UserRepository.getInstance().deletePlayer(player);
	}
}
