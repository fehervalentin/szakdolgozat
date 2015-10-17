package hu.elte.bfw1p6.poker.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;

public class SessionService {
	
	private Map<UUID, String> authenticatedUsers;
	
	public SessionService() {
		this.authenticatedUsers = new HashMap<>();
	}
	
	public boolean isAuthenticated(UUID uuid) {
		return authenticatedUsers.containsKey(uuid);
	}
	
	public PokerSession authenticate(String username, String password) throws PokerInvalidUserException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(username);
		if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
			throw new PokerInvalidUserException("Hibás bejelentkezési adatok!");
		}
		invalidate(username);
		UUID uuid = UUID.randomUUID();
		authenticatedUsers.put(uuid, username);
		PokerSession pokerSession = new PokerSession(uuid, u.getPlayer());
		return pokerSession;
	}
	
	public void invalidate(UUID uuid) {
		authenticatedUsers.remove(uuid);
	}
	
	private void invalidate(String username) {
		authenticatedUsers.values().remove(username);
	}
	
	public String lookUpUserName(UUID uuid) {
		return authenticatedUsers.get(uuid);
	}
}