package hu.elte.bfw1p6.poker.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;

public class SessionService {
	
	private List<PokerSession> sessions;
	
	public SessionService() {
		sessions = new ArrayList<>();
	}
	
	public boolean isAuthenticated(PokerSession pokerSession) {
		return sessions.contains(pokerSession);
	}
	
	public PokerSession authenticate(String username, String password) throws PokerInvalidUserException, PokerDataBaseException {
		User u = UserRepository.getInstance().findUserByUserName(username);
		Player p = u.getPlayer();
		if (!BCrypt.checkpw(password, u.getPassword())) {
			throw new PokerInvalidUserException("Hibás bejelentkezési adatok!");
		}
		invalidate(username);
		PokerSession pokerSession = new PokerSession(UUID.randomUUID(), p);
		sessions.add(pokerSession);
		return pokerSession;
	}
	
	private void invalidate(String username) {
		for (int i = 0; i < sessions.size(); i++) {
			if (sessions.get(i).getPlayer().getUserName().equals(username)) {
				sessions.remove(i);
				return;
			}
		}
	}
	
	public void invalidate(PokerSession pokerSession) {
		sessions.remove(pokerSession);
	}
}
