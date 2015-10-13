package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.exception.PokerInvalidSession;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;


public class Model {
	
	private final String SESSION_ERR = "Hibás session!";

	private static Model instance = null;

	private PokerRemote pokerRemote;

	private UUID sessionId;

	private Model() {
		pokerRemote = RMIRepository.getInstance().getPokerRemote();
	}

	public static Model getInstance() {
		if(instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public void login(String username, String password) throws RemoteException, PokerInvalidUserException {
		sessionId = pokerRemote.login(username, password);
		RMIRepository.getInstance().setSessionId(sessionId);
	}

	public void registration(String username, String password) throws RemoteException, SQLException {
		pokerRemote.registration(username, password);
	}

	public void createTable(PokerTable t) throws RemoteException, PokerInvalidUserException, SQLException {
		pokerRemote.createTable(t);
	}

	public List<PokerTable> getTables() {
		List<PokerTable> tables = null;
		try {
			tables = pokerRemote.getTables();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}

	public void registerObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.registerObserver(sessionId, observer);
	}

	public void connectToTable() {

	}

	public void addObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.addObserver(observer);
	}

	public List<PokerTable> registerTableViewObserver(RemoteObserver observer) throws RemoteException {
		return pokerRemote.registerTableViewObserver(observer);
	}

	public void removeTableViewObserver(RemoteObserver observer) throws RemoteException {
		pokerRemote.removeTableViewObserver(observer);
	}

	public void logout() throws RemoteException, PokerInvalidSession {
		if (sessionId == null) {
			throw new PokerInvalidSession(SESSION_ERR);
		}
		pokerRemote.logout(sessionId);
		sessionId = null;
	}

	/*public List<Table> getTables() {
		return pokerRemote.getTables();
	}*/
}
