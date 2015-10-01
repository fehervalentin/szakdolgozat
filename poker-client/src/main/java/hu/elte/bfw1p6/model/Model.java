package hu.elte.bfw1p6.model;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import hu.elte.bfw1p6.model.entity.Player;
import hu.elte.bfw1p6.rmi.PokerRemote;
import hu.elte.bfw1p6.rmi.security.PokerLoginRemote;

public class Model {

	private Registry registry;
	private PokerLoginRemote pokerLoginRemote;
	private PokerRemote pokerRemote;
	private PokerProperties pokerProperties;

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

	public Player login(String username, String password) {
		//pokerRemote = pokerLoginRemote.login(username, password);
		return null;
	}
}
