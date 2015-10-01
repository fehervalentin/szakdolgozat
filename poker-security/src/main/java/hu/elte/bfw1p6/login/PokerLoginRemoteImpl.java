package hu.elte.bfw1p6.login;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javax.security.auth.Subject;

import hu.elte.bfw1p6.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.model.PokerProperties;
import hu.elte.bfw1p6.rmi.PokerRemote;
import hu.elte.bfw1p6.rmi.security.PokerLoginRemote;

public class PokerLoginRemoteImpl extends UnicastRemoteObject implements PokerLoginRemote {

	private static final long serialVersionUID = 1L;
	
	private PokerRemote pokeRemote;
	
	private PokerProperties pokerProperties;
	
	protected PokerLoginRemoteImpl(PokerRemote pokerRemote) throws RemoteException {
		this.pokerProperties = PokerProperties.getInstance();
		this.pokeRemote = pokerRemote;
		
		try {
			PokerLoginRemote pokerLoginRemote = (PokerLoginRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("port")));
			Registry registry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			try {
				registry.bind(pokerProperties.getProperty("name"), pokerLoginRemote);
			} catch (AlreadyBoundException e) {
				// e.printStackTrace();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("A szerver elindult");
	}

	@Override
	public PokerRemote login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException {
		Subject user = new Subject();
		user.getPrincipals().add(new PokerLoginPrincipal(username));
		String storedPassword = null;
		
		
		Properties passwords = new Properties();
		passwords.setProperty("password", "jancsika");
		storedPassword = passwords.getProperty("password");
		//TODO db-ből kell lekérni a hasht!
		
		
		if ((storedPassword == null) || (!storedPassword.equals(password))) {
			throw new PokerInvalidUserException(username);
		}
		return pokeRemote;
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
