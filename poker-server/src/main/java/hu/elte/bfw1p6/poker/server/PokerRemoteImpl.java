package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
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

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.PokerTableDAO;
import hu.elte.bfw1p6.poker.persist.dao.UserDAO;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.server.security.SessionService;

/**
 * A pókerszerver megvalósítása.
 * @author feher
 *
 */
public class PokerRemoteImpl extends Observable implements PokerRemote {

	private static final long serialVersionUID = -4495230178265270679L;

	private final String ERR_BAD_PW = "Hibás jelszó!";

	private PokerProperties pokerProperties;

	private SessionService sessionService;

	private List<PokerRemoteObserver> clients;

	private Hashtable<String, AbstractPokerTableServer> pokerTableservers;
	
	private PokerTableDAO pokerTableDAO;
	
	private UserDAO userDAO;
	
	private String initialBalance = "1000.00";

	public PokerRemoteImpl() throws RemoteException, PokerDataBaseException {
		this.pokerProperties = PokerProperties.getInstance();
		this.pokerTableDAO = new PokerTableDAO();
		this.userDAO = new UserDAO();
		this.sessionService = new SessionService(userDAO);
		this.clients = new ArrayList<>();
		this.pokerTableservers = new Hashtable<>();
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
			pokerTableservers.put(tables.get(i).getName(), apts);
		}
		try {
			System.out.println("***POKER SZERVER***");
			System.out.println(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			System.out.println(pokerProperties.getProperty("name"));
			
			Registry rmiRegistry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			PokerRemote pokerRemote = (PokerRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("rmiport")));
			rmiRegistry.bind(pokerProperties.getProperty("name"), pokerRemote);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		System.out.println("A szerver elindult");
	}

	@Override
	public synchronized void deleteTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableservers.remove(t.getName());
			pokerTableDAO.delete(t);
			this.setChanged();
			this.notifyObservers(getTables(uuid));
		}
	}

	@Override
	public synchronized void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
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
			pokerTableservers.put(tables.get(last).getName(), apts);
			this.setChanged();
			this.notifyObservers(getTables(uuid));
		}
	}

	@Override
	public synchronized void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableDAO.modify(t);
			this.notifyObservers();
		}
	}

	@Override
	public synchronized void modifyPassword(UUID uuid, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException {
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
	public List<PokerTable> getTables(UUID uuid) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			return pokerTableDAO.findAll();
		}
		return null;
	}

	@Override
	public PokerSession login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException, PokerDataBaseException {
		for (int i = clients.size() - 1; i >= 0; i--) {
			try {
				clients.get(i).update("ping");
			} catch (RemoteException e) {
				clients.remove(i);
				sessionService.invalidate(username);
			}
		}
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
	public boolean isAdmin(UUID uuid) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			String username = sessionService.lookUpUserName(uuid);
			return userDAO.findByUserName(username).getAdmin();
		}
		return false;
	}

	@Override
	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		User u = new User(username);
		u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		u.setBalance(new BigDecimal(initialBalance));
		u.setAdmin(false);
		userDAO.save(u);
	}

	@Override
	public List<PokerTable> registerTableViewObserver(UUID uuid, PokerRemoteObserver observer) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		clients.add(observer);
		return getTables(uuid);
	}

	@Override
	public void removeTableViewObserver(PokerRemoteObserver observer) {
		clients.remove(observer);
	}

	@Override
	public void sendPlayerCommand(UUID uuid, PokerTable t, PokerRemoteObserver client, PlayerCommand playerCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableservers.get(t.getName()).receivedPlayerCommand(client, playerCommand);
		}
	}

	@Override
	public void connectToTable(UUID uuid, PokerTable t, PokerRemoteObserver client) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			AbstractPokerTableServer pts = pokerTableservers.get(t.getName());
			pts.join(client, sessionService.lookUpUserName(uuid));
		}
	}

	@Override
	public void deletePlayer(UUID uuid, PokerPlayer player) throws RemoteException, PokerDataBaseException {
		userDAO.deletePlayer(player);
	}

	@Override
	public BigDecimal refreshBalance(UUID uuid) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		return userDAO.findByUserName(sessionService.lookUpUserName(uuid)).getPlayer().getBalance();
	}

	@Override
	public List<PokerPlayer> getUsers() throws RemoteException, PokerDataBaseException {
		List<PokerPlayer> pokerPlayers = new ArrayList<>();
		userDAO.findAll().forEach(user -> pokerPlayers.add(user.getPlayer()));
		return pokerPlayers;
	}
}