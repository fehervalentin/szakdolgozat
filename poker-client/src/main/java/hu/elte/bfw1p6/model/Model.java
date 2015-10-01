package hu.elte.bfw1p6.model;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import hu.elte.bfw1p6.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.rmi.security.PokerLoginRemote;

public class Model {

	private Registry registry;
	private PokerLoginRemote pokerRemote;
	private PokerProperties pokerProperties;

	public Model() {
		pokerProperties = PokerProperties.getInstance();
		try {
			registry = LocateRegistry.getRegistry(1099);
			pokerRemote = (PokerLoginRemote) registry.lookup(pokerProperties.getProperty("name"));
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
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (PokerInvalidUserException e) {
			e.printStackTrace();
		}
		System.out.println(succ + " lol");
		return succ;
	}
}
