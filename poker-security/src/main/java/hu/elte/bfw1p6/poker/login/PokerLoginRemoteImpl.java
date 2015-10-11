package hu.elte.bfw1p6.poker.login;

import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.user.UserBuilder;
import hu.elte.bfw1p6.poker.persist.user.UserRepository;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;
import hu.elte.bfw1p6.poker.security.service.SessionService;


public class PokerLoginRemoteImpl extends UnicastRemoteObject implements PokerLoginRemote {

	private static final long serialVersionUID = 1L;

	private PokerRemote pokerRemote;

	private PokerProperties pokerProperties;

	private SessionService sessionService;

	public PokerLoginRemoteImpl(PokerRemote pokerRemote) throws RemoteException {
		
		//URL url = getClass().getClassLoader().getResource("client.policy");
		//System.setProperty("java.security.policy", url.toString()); 
		/*if (System.getSecurityManager() == null) {
		      System.setSecurityManager(new RMISecurityManager());
		}*/	

		this.pokerProperties = PokerProperties.getInstance();
		this.pokerRemote = pokerRemote;
		this.sessionService = new SessionService();

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
		User u = UserRepository.findUserByUserName(username);
		//			User u = userDAO.findUserByUserName(username);
		if (!BCrypt.checkpw(password, u.getPassword())) {
			throw new PokerInvalidUserException("Hibás bejelentkezési adatok!");
		}
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
	public void registration(String username, String password) throws RemoteException, SQLException {
		User u = UserBuilder.geInstance().buildUser(username, password);
		UserRepository.save(u);
	}
}