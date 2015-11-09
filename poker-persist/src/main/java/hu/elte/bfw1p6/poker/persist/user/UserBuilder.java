package hu.elte.bfw1p6.poker.persist.user;

import java.math.BigDecimal;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.model.entity.User;

public class UserBuilder {
	
	private static UserBuilder instance = null;
	
	private UserBuilder() {
		
	}
	
	public static UserBuilder geInstance() {
		if (instance == null) {
			instance = new UserBuilder();
		}
		return instance;
	}
	
	public User buildUser(String username, String password) {
		User u = new User(username);
		u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		u.setBalance(new BigDecimal(10000.00));
		u.setAdmin(false);
		return u;
	}
}
