package hu.elte.bfw1p6.poker.server.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.UserDAO;

public class SessionService {
	
	private Map<UUID, String> authenticatedUsers;
	
	private UserDAO userDAO;
	
	public SessionService(UserDAO userDAO) {
		this.userDAO = userDAO;
		this.authenticatedUsers = new HashMap<>();
	}
	
	public boolean isAuthenticated(UUID uuid) {
		return authenticatedUsers.containsKey(uuid);
	}
	
	public boolean isAuthenicated(String userName) {
		return authenticatedUsers.values().contains(userName);
	}
	
	public PokerSession authenticate(String username, String password) throws PokerInvalidUserException, PokerDataBaseException {
		User u = userDAO.findByUserName(username);
		if (u == null || !BCrypt.checkpw(password, u.getPassword()) || authenticatedUsers.values().contains(username)) {
			throw new PokerInvalidUserException("Hibás bejelentkezési adatok!");
		}
		UUID uuid = UUID.randomUUID();
		authenticatedUsers.put(uuid, username);
		PokerSession pokerSession = new PokerSession(uuid, u.getPlayer());
		return pokerSession;
	}
	
	public void invalidate(String username) {
		authenticatedUsers.values().remove(username);
	}
	
	public void invalidate(UUID uuid) {
		authenticatedUsers.remove(uuid);
	}
	
	public String lookUpUserName(UUID uuid) {
		return authenticatedUsers.get(uuid);
	}
}