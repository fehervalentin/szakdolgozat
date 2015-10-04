package hu.elte.bfw1p6.login;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import hu.elte.bfw1p6.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.model.PokerProperties;
import hu.elte.bfw1p6.persist.dao.UserDAO;
import hu.elte.bfw1p6.rmi.PokerRemote;
import hu.elte.bfw1p6.rmi.security.PokerLoginRemote;
import hu.elte.bfw1p6.security.service.SessionService;

public class PokerLoginRemoteImpl extends UnicastRemoteObject implements PokerLoginRemote {

	private static final long serialVersionUID = 1L;

	private PokerRemote pokerRemote;

	private PokerProperties pokerProperties;

	private SessionService sessionService;
	
	private UserDAO userDAO;
	
	public PokerLoginRemoteImpl(PokerRemote pokerRemote) throws RemoteException {

		this.pokerProperties = PokerProperties.getInstance();
		this.pokerRemote = pokerRemote;
		this.sessionService = new SessionService();
		this.userDAO = new UserDAO();

		try {
			//PokerLoginRemote pokerLoginRemote = (PokerLoginRemote) UnicastRemoteObject.exportObject(this, Integer.valueOf(pokerProperties.getProperty("port")));
			Registry registry = LocateRegistry.createRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			try {
				registry.bind(pokerProperties.getProperty("name"), this);
			} catch (AlreadyBoundException e) {
				// e.printStackTrace();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("A szerver elindult");
	}

	@Override
	public UUID login(String username, String password) throws RemoteException, SecurityException, PokerInvalidUserException {
		return sessionService.authenticate(username, password);
	}
	
	

	@Override
	public boolean shutDown(UUID uuid) throws RemoteException {
		try {
			UnicastRemoteObject.unexportObject(this, true);
			System.out.println("A szerver leállt");
			return true;
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public PokerRemote getPokerRemote(UUID uuid) throws RemoteException, SecurityException, PokerInvalidUserException {
		if (sessionService.isAuthenticated(uuid)) {
			return pokerRemote;
		}
		throw new PokerInvalidUserException("cumi");
	}

	@Override
	public void logout(UUID uuid) throws RemoteException {
		sessionService.invalidate(uuid);
	}

	@Override
	public boolean isAdmin(UUID uuid) throws RemoteException {
		// TODO DAO-tól elkérni servicen keresztül?
		return false;
	}
	
	@Override
	public boolean registration(String username, String password) throws RemoteException {
		userDAO.persistUser(username, password);
		return true;
	}
}