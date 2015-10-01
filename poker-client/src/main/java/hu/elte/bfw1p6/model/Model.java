package hu.elte.bfw1p6.model;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import hu.elte.bfw1p6.rmi.PokerRemote;

public class Model {

	private Registry registry;
	private PokerRemote pokerRemote;
	private PokerProperties pokerProperties;

	public Model() {
		pokerProperties = PokerProperties.getInstance();
		try {
			registry = LocateRegistry.getRegistry(1099);
			pokerRemote = (PokerRemote) registry.lookup(pokerProperties.getProperty("name"));
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	public boolean login(LoginUser loginUser) {
		boolean succ = false;
		try {
			succ = pokerRemote.shutDown();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println(succ + " lol");
		return succ;
	}
}
