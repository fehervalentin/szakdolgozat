package hu.elte.bfw1p6.poker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import hu.elte.bfw1p6.poker.model.entity.User;

/**
 * A userek sessionje.
 * @author feher
 *
 */
public class PokerSession implements Serializable {

	private static final long serialVersionUID = -7519620286610273299L;
	
	/**
	 * Session azonosító.
	 */
	private UUID id;
	
	/**
	 * A póker játékos.
	 */
	private User user;
	
	public PokerSession(UUID id, User user) {
		this.id = id;
		this.user = user;
	}
	
	public UUID getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}

	public void refreshBalance(BigDecimal refreshBalance) {
		user.setBalance(refreshBalance);
	}
}