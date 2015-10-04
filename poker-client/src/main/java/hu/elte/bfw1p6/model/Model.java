package hu.elte.bfw1p6.model;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;


public class Model {

	private Registry registry;
	private PokerLoginRemote pokerLoginRemote;
	private PokerRemote pokerRemote;
	private PokerProperties pokerProperties;
	private UUID sessionId;

	public Model() {
		pokerProperties = PokerProperties.getInstance();
		try {
			registry = LocateRegistry.getRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			pokerLoginRemote = (PokerLoginRemote) registry.lookup(pokerProperties.getProperty("name"));
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String password) {
		try {
			sessionId = pokerLoginRemote.login(username, password);
			pokerRemote = pokerLoginRemote.getPokerRemote(sessionId);
			System.out.println(pokerRemote.sayHello());
		} catch (RemoteException | SecurityException | PokerInvalidUserException e) {
			e.printStackTrace();
		}
	}

	public boolean registration(String username, String password) {
		try {
			pokerLoginRemote.registration(username, password);
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*public List<Table> getTables() {
		return pokerRemote.getTables();
	}*/
}
