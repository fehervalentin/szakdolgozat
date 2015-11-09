package hu.elte.bfw1p6.poker.client.model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

/**
 * Kliens oldali model, amely a kisebb műveletek hívásáért felelős.
 * @author feher
 *
 */
public class Model {
	
	private static Model instance = null;
	
	private static PokerTable paramPokerTable;

	private static PokerRemote pokerRemote;

	private static PokerSession pokerSession;
	
	private final String SVNAME;
	private final String PORT;

	private PokerProperties pokerProperties;

	private Model() throws MalformedURLException, RemoteException, NotBoundException {
		pokerProperties = PokerProperties.getInstance();
		SVNAME =  pokerProperties.getProperty("name");
		PORT = pokerProperties.getProperty("rmiport");
		pokerRemote = (PokerRemote) Naming.lookup("//localhost:" + PORT + "/" + SVNAME);
	}
	
	public static Model getInstance() throws MalformedURLException, RemoteException, NotBoundException {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	/**
	 * Bejelentkezés a pókerjátékba.
	 * @param username a felhasználónév
	 * @param password a jelszó (plain textben)
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerInvalidUserException
	 */
	public void login(String username, String password) throws RemoteException, PokerDataBaseException, PokerInvalidUserException {
		pokerSession = pokerRemote.login(username, password);
	}

	/**
	 * Regisztráció a pókerjátékba.
	 * @param username a felhasználónév
	 * @param password a jelszó (plain textben)
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		pokerRemote.registration(username, password);
	}

	/**
	 * Új játéktábla létrehozása.
	 * @param t a létrehozandó játéktábla szerver
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerUnauthenticatedException
	 */
	public void createTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		pokerRemote.createTable(pokerSession.getId(), t);
	}

	/**
	 * A letárolt összes játéktábla lekérdezése.
	 * @return a letárolt játéktáblák
	 * @throws PokerDataBaseException
	 * @throws PokerUnauthenticatedException
	 */
	public List<PokerTable> getTables() throws PokerDataBaseException, PokerUnauthenticatedException {
		List<PokerTable> tables = null;
		try {
			tables = pokerRemote.getTables(pokerSession.getId());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}

	public List<PokerTable> registerTableViewObserver(PokerRemoteObserver observer) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		return pokerRemote.registerTableViewObserver(pokerSession.getId(), observer);
	}

	public void removeTableViewObserver(PokerRemoteObserver observer) throws RemoteException {
		pokerRemote.removeTableViewObserver(observer);
	}

	public void logout() throws RemoteException {
		pokerRemote.logout(pokerSession.getId());
		pokerSession = null;
	}
	
	public void setParameterPokerTable(PokerTable paramPokerTable) {
		Model.paramPokerTable = paramPokerTable;
	}
	
	public static PokerTable getParamPokerTable() {
		return paramPokerTable;
	}
	
	public static PokerSession getPokerSession() {
		return pokerSession;
	}

	public static PokerRemote getPokerRemote() {
		return pokerRemote;
	}
	
	public void modifyTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		pokerRemote.modifyTable(pokerSession.getId(), t);
	}

	public void deleteTable(PokerTable pokerTable) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		pokerRemote.deleteTable(pokerSession.getId(), pokerTable);
	}

	public void modifyPassword(String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException {
		pokerRemote.modifyPassword(pokerSession.getId(), oldPassword, newPassword);
	}
	
	public boolean isAdmin() throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException {
		return pokerRemote.isAdmin(pokerSession.getId());
	}
	
	public PokerPlayer getPlayer() {
		return pokerSession.getPlayer();
	}

	public List<PokerPlayer> getUsers() throws PokerDataBaseException, RemoteException {
		return pokerRemote.getUsers();
	}
}
