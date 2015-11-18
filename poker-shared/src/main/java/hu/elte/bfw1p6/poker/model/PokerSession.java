package hu.elte.bfw1p6.poker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

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
	private PokerPlayer player;
	
	public PokerSession(UUID id, PokerPlayer player) {
		this.id = id;
		this.player = player;
	}
	
	public UUID getId() {
		return id;
	}
	
	public PokerPlayer getPlayer() {
		return player;
	}

	public void refreshBalance(BigDecimal refreshBalance) {
		player.setBalance(refreshBalance);
	}
}