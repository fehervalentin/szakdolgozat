package hu.elte.bfw1p6.poker.client.repository;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import hu.elte.bfw1p6.poker.model.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;

public class RMIRepository {
	private static RMIRepository instance = null;

	private Registry registry;
	private PokerLoginRemote pokerLoginRemote;
	private PokerRemote pokerRemote;

	private PokerProperties pokerProperties;

	private RMIRepository() {
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

	public static RMIRepository getInstance() {
		if(instance == null) {
			instance = new RMIRepository();
		}
		return instance;
	}
	
	public PokerLoginRemote getPokerLoginRemote() {
		return pokerLoginRemote;
	}
	
	public PokerRemote getPokerRemote() {
		return pokerRemote;
	}
	
	public void setPokerRemote(PokerRemote pokerRemote) {
		this.pokerRemote = pokerRemote;
	}
}
