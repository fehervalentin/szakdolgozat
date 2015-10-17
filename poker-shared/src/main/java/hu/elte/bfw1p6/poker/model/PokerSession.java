package hu.elte.bfw1p6.poker.model;

import java.io.Serializable;
import java.util.UUID;

import hu.elte.bfw1p6.poker.model.entity.Player;

public class PokerSession implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private Player player;
	
	public PokerSession(UUID id, Player player) {
		this.id = id;
		this.player = player;
	}
	
	public UUID getId() {
		return id;
	}
	
	public Player getPlayer() {
		return player;
	}
}
