package hu.elte.bfw1p6.poker.server.security;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.UserDAO;
import hu.elte.bfw1p6.poker.session.PokerSession;

/**
 * A kliensek sessionjeit tartja nyilván és kezeli.
 * @author feher
 *
 */
public class SessionService {
	
	private final String ERR_BAD_AUTH = "Hibás bejelentkezési adatok!";
	
	private List<PokerSession> sessions;
	
	private UserDAO userDAO;
	
	public SessionService(UserDAO userDAO) {
		this.userDAO = userDAO;
		this.sessions = new ArrayList<>();
	}
	
	public boolean isAuthenticated(UUID uuid) {
		return sessions.stream().anyMatch(session -> session.getId().equals(uuid));
	}
	
	public boolean isAuthenicated(String userName) {
		return sessions.stream().anyMatch(session -> session.getUser().getUserName().equals(userName));
	}
	
	public synchronized PokerSession authenticate(String userName, String password) throws PokerInvalidUserException, PokerDataBaseException {
		User u = userDAO.findByUserName(userName);
		if (u == null || !BCrypt.checkpw(password, u.getPassword()) || sessions.stream().anyMatch(session -> session.getUser().getUserName().equals(userName))) {
			throw new PokerInvalidUserException(ERR_BAD_AUTH);
		}
		UUID uuid = UUID.randomUUID();
		PokerSession pokerSession = new PokerSession(uuid, u);
		sessions.add(pokerSession);
		return pokerSession;
	}
	
	public synchronized void invalidate(int i) {
		sessions.remove(i);
	}
	
	public synchronized void invalidate(UUID uuid) {
		sessions.removeIf(session -> session.getId().equals(uuid));
	}
	
	public String lookUpUserName(UUID uuid) {
		return sessions.stream().filter(session -> session.getId().equals(uuid)).findFirst().get().getUser().getUserName();
	}
}