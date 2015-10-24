package hu.elte.bfw1p6.poker.model;

import java.io.Serializable;
import java.util.UUID;

import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

public class PokerSession implements Serializable {

	private static final long serialVersionUID = -7519620286610273299L;
	
	private UUID id;
	
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
	
	public void setPlayer(PokerPlayer player) {
		this.player = player;
	}
}
