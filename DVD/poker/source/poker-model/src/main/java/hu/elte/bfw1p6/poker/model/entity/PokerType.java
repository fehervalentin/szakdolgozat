package hu.elte.bfw1p6.poker.model.entity;

/**
 * A póker játékstílusok definíciója.
 * @author feher
 *
 */
public enum PokerType {
	
	HOLDEM((byte)1, "HOLDEM", 5, 2),
	CLASSIC((byte)2, "CLASSIC", 0, 5);
	
	private byte id;
	private String name;
	private int cardsToHouse;
	private int cardsToPlayers;

	private PokerType(byte id, String name, int cardsToHouse, int cardsToPlayers) {
		this.id = id;
		this.name = name;
		this.cardsToHouse = cardsToHouse;
		this.cardsToPlayers = cardsToPlayers;
	}
	public String getName() {
		return name;
	}
	public int getCardsToHouse() {
		return cardsToHouse;
	}
	public int getCardsToPlayers() {
		return cardsToPlayers;
	}
	public int getId() {
		return id;
	}
}