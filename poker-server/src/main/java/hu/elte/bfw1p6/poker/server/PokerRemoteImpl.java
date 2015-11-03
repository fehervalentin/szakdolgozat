package hu.elte.bfw1p6.poker.server;

import java.io.Serializable;
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

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableListerObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
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
import hu.elte.bfw1p6.poker.persist.repository.PokerTableRepository;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.persist.user.UserBuilder;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.security.service.SessionService;

public class PokerRemoteImpl extends Observable implements PokerRemote, Serializable {

	private static final long serialVersionUID = 1L;

	private final String ERR_BAD_PW = "Hibás jelszó!";

	private PokerProperties pokerProperties;

	private SessionService sessionService;

	private List<TableListerObserver> tlos;

	private Hashtable<String, AbstractPokerTableServer> pokerTableservers;

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
		pokerTableservers.put(tables.get(1).getName(), new ClassicPokerTableServer(tables.get(1)));

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
			PokerTableRepository.getInstance().deleteTable(t);
			this.setChanged();
			this.notifyObservers(getTables(uuid));
		}
	}

	@Override
	public synchronized void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			PokerTableRepository.getInstance().save(t);
			this.setChanged();
			this.notifyObservers(getTables(uuid));
		}
	}

	@Override
	public synchronized void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			PokerTableRepository.getInstance().modify(t);
			this.notifyObservers();
		}
	}

	@Override
	public synchronized void modifyPassword(UUID uuid, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			String userName = sessionService.lookUpUserName(uuid);
			User u = UserRepository.getInstance().findByUserName(userName);
			if (BCrypt.checkpw(oldPassword, u.getPassword())) {
				newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
				UserRepository.getInstance().modifyPassword(userName, newPassword);
			} else {
				throw new PokerInvalidPassword(ERR_BAD_PW);
			}
		}
	}

	@Override
	public List<PokerTable> getTables(UUID uuid) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			return PokerTableRepository.getInstance().findAll();
		}
		return null;
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
	public boolean isAdmin(UUID uuid) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException {
		if (sessionService.isAuthenticated(uuid)) {
			String username = sessionService.lookUpUserName(uuid);
			User u = UserRepository.getInstance().findByUserName(username);
			return u.getAdmin();
		}
		return false;
	}

	@Override
	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		User u = UserBuilder.geInstance().buildUser(username, password);
		UserRepository.getInstance().save(u);
	}

	@Override
	public List<PokerTable> registerTableViewObserver(UUID uuid, RemoteObserver observer) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		TableListerObserver tvo = new TableListerObserver(observer);
		this.addObserver(tvo);
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
	public void sendPlayerCommand(UUID uuid, PokerTable t, RemoteObserver client, PlayerCommand playerCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		if (sessionService.isAuthenticated(uuid)) {
			pokerTableservers.get(t.getName()).receivePlayerCommand(client, playerCommand);
		}

	}

	@Override
	public void connectToTable(UUID uuid, PokerTable t, RemoteObserver client) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		if (sessionService.isAuthenticated(uuid)) {
			AbstractPokerTableServer pts = pokerTableservers.get(t.getName());
			pts.join(client, sessionService.lookUpUserName(uuid));
		}
	}

	@Override
	public void deletePlayer(UUID uuid, PokerPlayer player) throws RemoteException, PokerDataBaseException {
		UserRepository.getInstance().deletePlayer(player);
	}

	@Override
	public BigDecimal refreshBalance(UUID uuid) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		return UserRepository.getInstance().findByUserName(sessionService.lookUpUserName(uuid)).getPlayer().getBalance();
	}
}
