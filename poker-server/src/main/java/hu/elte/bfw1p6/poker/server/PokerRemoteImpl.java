package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTableDeleteException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.PokerTableDAO;
import hu.elte.bfw1p6.poker.persist.dao.UserDAO;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.server.security.SessionService;
import hu.elte.bfw1p6.poker.session.PokerSession;

/**
 * A pókerszerver megvalósítása.
 * @author feher
 *
 */
public class PokerRemoteImpl extends Observable implements PokerRemote {

	private static final long serialVersionUID = -4495230178265270679L;

	private final String ERR_BAD_PW = "Hibás jelszó!";
	private final String ERR_TABLE_DELETE = "Az asztal nem törölhető/módosítható: nem üres!";
	
	private final String INITIAL_BALANCE = "1000.00";

	/**
	 * A szerver hálózati beállításai. (Melyik porton és milyen néven kell elérhetővé tenni a szervert.)
	 */
	private PokerProperties pokerProperties;

	/**
	 * Session kezelő objektum.
	 */
	private SessionService sessionService;

	/**
	 * A csatlakozott kliensek
	 */
	private List<PokerRemoteObserver> clients;

	/**
	 * Az éppen futó játéktábla szerverek.
	 */
	private List<AbstractPokerTableServer> pokerTableservers;
	
	/**
	 * A játéktáblákat kezelő DAO.
	 */
	private PokerTableDAO pokerTableDAO;
	
	/**
	 * A felhasználókat kezelő DAO.
	 */
	private UserDAO userDAO;

	public PokerRemoteImpl() throws RemoteException, PokerDataBaseException {
		this.pokerProperties = PokerProperties.getInstance();
		this.pokerTableDAO = new PokerTableDAO();
		this.userDAO = new UserDAO();
		this.sessionService = new SessionService(userDAO);
		this.clients = new ArrayList<>();
		this.pokerTableservers = new ArrayList<>();
		List<PokerTable> tables = pokerTableDAO.findAll();
		
		for (int i = 0; i < tables.size(); i++) {
			AbstractPokerTableServer apts;
			switch (tables.get(i).getPokerType()) {
			case HOLDEM:
				apts = new HoldemPokerTableServer(tables.get(i));
				break;
			case CLASSIC:
				apts = new ClassicPokerTableServer(tables.get(i));
				break;
			default:
				throw new IllegalArgumentException();
			}
			pokerTableservers.add(apts);
		}
		try {
			Registry rmiRegistry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			PokerRemote pokerRemote = (PokerRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("rmiport")));
			rmiRegistry.bind(pokerProperties.getProperty("name"), pokerRemote);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		System.out.println("***POKER SZERVER***");
		System.out.println("Port:" + Integer.valueOf(pokerProperties.getProperty("rmiport")));
		System.out.println("Szerver név: " + pokerProperties.getProperty("name"));
		System.out.println("A szerver elindult");
	}

	@Override
	public synchronized void deleteTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerTableDeleteException {
		if (sessionService.isAuthenticated(uuid)) {
			AbstractPokerTableServer apts = getAbstractPokerTableServerByTableName(t.getId());
			if (apts.getPlayersCount() > 0) {
				throw new PokerTableDeleteException(ERR_TABLE_DELETE);
			} else {
				pokerTableDAO.delete(t);
				pokerTableservers.remove(getAbstractPokerTableServerByTableName(t.getId()));
				List<PokerTable> tables = getTables(uuid);
				for (int i = 0; i < clients.size(); i++) {
					clients.get(i).update(tables);
				}
			}
		}
	}

	@Override
	public synchronized void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableDAO.save(t);
			List<PokerTable> tables = pokerTableDAO.findAll();
			AbstractPokerTableServer apts;
			int last = tables.size() - 1;
			switch (tables.get(last).getPokerType()) {
			case HOLDEM:
				apts = new HoldemPokerTableServer(tables.get(last));
				break;
			case CLASSIC:
				apts = new ClassicPokerTableServer(tables.get(last));
				break;
			default:
				throw new IllegalArgumentException();
			}
			pokerTableservers.add(apts);
			this.setChanged();
			this.notifyObservers(getTables(uuid));
		}
	}

	@Override
	public synchronized void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerTableDeleteException {
		if (sessionService.isAuthenticated(uuid)) {
			AbstractPokerTableServer apts = getAbstractPokerTableServerByTableName(t.getId());
			if (apts.getPlayersCount() > 0) {
				throw new PokerTableDeleteException(ERR_TABLE_DELETE);
			} else {
				pokerTableDAO.modify(t);
				switch (t.getPokerType()) {
				case HOLDEM:
					apts = new HoldemPokerTableServer(t);
					break;
				case CLASSIC:
					apts = new ClassicPokerTableServer(t);
					break;
				default:
					throw new IllegalArgumentException();
				}
				int index = pokerTableservers.indexOf(getAbstractPokerTableServerByTableName(t.getId()));
				pokerTableservers.set(index, apts);
				List<PokerTable> tables = getTables(uuid);
				for (int i = 0; i < clients.size(); i++) {
					clients.get(i).update(tables);
				}
			}
		}
	}

	@Override
	public synchronized void modifyPassword(UUID uuid, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword {
		if (sessionService.isAuthenticated(uuid)) {
			String userName = sessionService.lookUpUserName(uuid);
			User u = userDAO.findByUserName(userName);
			if (BCrypt.checkpw(oldPassword, u.getPassword())) {
				newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
				userDAO.modifyPassword(userName, newPassword);
			} else {
				throw new PokerInvalidPassword(ERR_BAD_PW);
			}
		}
	}

	@Override
	public List<PokerTable> getTables(UUID uuid) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			return pokerTableDAO.findAll();
		}
		return null;
	}

	@Override
	public synchronized PokerSession login(String userName, String password) throws RemoteException, PokerInvalidUserException, PokerDataBaseException {
		purgeClients();
		return sessionService.authenticate(userName, password);
	}
	
	/**
	 * Akinél megszakadt a kapcsolat, azt kidobjuk.
	 */
	private void purgeClients() {
		for (int i = clients.size() - 1; i >= 0; i--) {
			try {
				clients.get(i).update("ping");
			} catch (RemoteException e) {
				clients.remove(i);
				sessionService.invalidate(i);
			}
		}
	}

	@Override
	public synchronized void logout(UUID uuid) throws RemoteException {
		sessionService.invalidate(uuid);
	}

	@Override
	public boolean isAdmin(UUID uuid) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			String username = sessionService.lookUpUserName(uuid);
			return userDAO.findByUserName(username).getAdmin();
		}
		return false;
	}

	@Override
	public synchronized void registration(String userName, String password) throws RemoteException, PokerDataBaseException {
		User u = new User();
		u.setUserName(userName);
		u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		u.setBalance(new BigDecimal(INITIAL_BALANCE));
		u.setAdmin(false);
		userDAO.save(u);
	}

	@Override
	public synchronized List<PokerTable> registerTableViewObserver(UUID uuid, PokerRemoteObserver observer) throws RemoteException, PokerDataBaseException {
		clients.add(observer);
		return getTables(uuid);
	}

	@Override
	public synchronized void sendPlayerCommand(UUID uuid, PokerTable t, PokerRemoteObserver client, PlayerCommand playerCommand) throws RemoteException, PokerDataBaseException, PokerUserBalanceException {
		if (sessionService.isAuthenticated(uuid)) {
			getAbstractPokerTableServerByTableName(t.getId()).receivedPlayerCommand(client, playerCommand);
		}
	}

	@Override
	public synchronized void connectToTable(UUID uuid, PokerTable t, PokerRemoteObserver client) throws RemoteException, PokerTooMuchPlayerException {
		if (sessionService.isAuthenticated(uuid)) {
			AbstractPokerTableServer pts = getAbstractPokerTableServerByTableName(t.getId());
			pts.join(client, sessionService.lookUpUserName(uuid));
		}
	}

	@Override
	public synchronized BigDecimal refreshBalance(UUID uuid) throws RemoteException, PokerDataBaseException {
		return userDAO.findByUserName(sessionService.lookUpUserName(uuid)).getBalance();
	}

	@Override
	public List<User> getUsers(UUID uuid) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			return userDAO.findAll().stream().collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public synchronized boolean canSitIn(UUID uuid, PokerTable paramPokerTable) throws RemoteException {
		if (sessionService.isAuthenticated(uuid)) {
			return getAbstractPokerTableServerByTableName(paramPokerTable.getId()).canSitIn();
		}
		return false;
	}
	
	private AbstractPokerTableServer getAbstractPokerTableServerByTableName(Integer id) {
		return pokerTableservers.stream().filter(sv -> sv.getId().equals(id)).findFirst().get();
	}

	@Override
	public void modifyUser(UUID uuid, User u) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			userDAO.modify(u);
		}
	}

	@Override
	public void deleteUser(UUID uuid, User u) throws RemoteException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			userDAO.delete(u);
		}
	}
}