package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidSession;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;


public class Model {
	
	private final String SESSION_ERR = "Hibás session!";

	private static Model instance = null;
	
	private PokerTable paramPokerTable;

	private PokerRemote pokerRemote;

	private PokerSession pokerSession;

	private Model() {
		pokerRemote = RMIRepository.getInstance().getPokerRemote();
	}

	public static Model getInstance() {
		if(instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public void login(String username, String password) throws RemoteException, PokerInvalidUserException, PokerDataBaseException {
		pokerSession = pokerRemote.login(username, password);
	}

	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		pokerRemote.registration(username, password);
	}

	public void createTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		pokerRemote.createTable(pokerSession, t);
	}

	public List<PokerTable> getTables() throws PokerDataBaseException {
		List<PokerTable> tables = null;
		try {
			tables = pokerRemote.getTables(pokerSession);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}

	public void registerObserver(RemoteObserver observer) throws RemoteException, PokerDataBaseException {
		pokerRemote.registerObserver(pokerSession, observer);
	}

	public void connectToTable() {

	}

	public void addObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.addObserver(pokerSession, observer);
	}

	public List<PokerTable> registerTableViewObserver(RemoteObserver observer) throws RemoteException, PokerDataBaseException {
		return pokerRemote.registerTableViewObserver(pokerSession, observer);
	}

	public void removeTableViewObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.removeTableViewObserver(observer);
	}

	public void logout() throws RemoteException, PokerInvalidSession {
		if (pokerSession == null) {
			throw new PokerInvalidSession(SESSION_ERR);
		}
		pokerRemote.logout(pokerSession);
		pokerSession = null;
	}
	
	public void setParameterPokerTable(PokerTable paramPokerTable) {
		this.paramPokerTable = paramPokerTable;
	}
	
	public PokerTable getParamPokerTable() {
		return paramPokerTable;
	}

	public void modifyTable(PokerTable t) throws RemoteException, PokerDataBaseException {
		pokerRemote.modifyTable(pokerSession, t);
	}

	public void deleteTable(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		pokerRemote.deleteTable(pokerSession, pokerTable);
	}

	public void modifyPassword(String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException {
		pokerRemote.modifyPassword(pokerSession, oldPassword, newPassword);
	}

	/*public List<Table> getTables() {
		return pokerRemote.getTables();
	}*/
}
