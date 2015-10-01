package hu.elte.bfw1p6.server;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.model.LoginUser;
import hu.elte.bfw1p6.model.PokerProperties;
import hu.elte.bfw1p6.rmi.PokerRemote;

public class PokerServer implements PokerRemote {

	private static PokerProperties pokerProperties;
	
	public PokerServer() {
		pokerProperties = PokerProperties.getInstance();
		try {
			PokerRemote pokerRemote = (PokerRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("port")));
			Registry registry = LocateRegistry.createRegistry(1099);
			try {
				registry.bind(pokerProperties.getProperty("name"), pokerRemote);
			} catch (AlreadyBoundException e) {
				// e.printStackTrace();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("A szerver elindult");
	}

	@Override
	public boolean login(LoginUser loginUser) throws RemoteException {
		System.out.println("meghivta");
		return true;
	}

	@Override
	public boolean shutDown() throws RemoteException {
		try {
			UnicastRemoteObject.unexportObject(this, true);
			System.out.println("A szerver leállt");
			return true;
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
		return false;
	}
}
