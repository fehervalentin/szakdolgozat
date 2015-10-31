package hu.elte.bfw1p6.poker.client.model;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
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
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
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

	public void login(String username, String password) throws RemoteException, PokerDataBaseException, PokerInvalidUserException {
		pokerSession = pokerRemote.login(username, password);
		RMIRepository.getInstance().setPokerSession(pokerSession);
	}

	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		pokerRemote.registration(username, password);
	}

	public void createTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		pokerRemote.createTable(pokerSession.getId(), t);
	}

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

	public List<PokerTable> registerTableViewObserver(RemoteObserver observer) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException {
		return pokerRemote.registerTableViewObserver(pokerSession.getId(), observer);
	}

	public void removeTableViewObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.removeTableViewObserver(observer);
	}

	public void logout() throws RemoteException, PokerInvalidSession {
		if (pokerSession == null) {
			throw new PokerInvalidSession(SESSION_ERR);
		}
		pokerRemote.logout(pokerSession.getId());
		pokerSession = null;
	}
	
	public void setParameterPokerTable(PokerTable paramPokerTable) {
		this.paramPokerTable = paramPokerTable;
	}
	
	public PokerTable getParamPokerTable() {
		return paramPokerTable;
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
	
	public PokerPlayer getPlayer() {
		return pokerSession.getPlayer();
	}
	
	public PokerSession getPokerSession() {
		return pokerSession;
	}
}
