package hu.elte.bfw1p6.poker.model.entity;

public enum PokerType {
	
	HOLDEM(1, "HOLDEM", 5, 2),
	OMAHA(2, "OMAHA", 5, 2);
	
	private int id;
	private String name;
	private int cardsToHouse;
	private int cardsToPlayers;

	private PokerType(int id, String name, int cardsToHouse, int cardsToPlayers) {
		this.id = id;
		this.name = name;
		this.cardsToHouse = cardsToHouse;
		this.cardsToPlayers = cardsToPlayers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCardsToHouse() {
		return cardsToHouse;
	}
	public void setCardsToHouse(int cardsToHouse) {
		this.cardsToHouse = cardsToHouse;
	}
	public int getCardsToPlayers() {
		return cardsToPlayers;
	}
	public void setCardsToPlayers(int cardsToPlayers) {
		this.cardsToPlayers = cardsToPlayers;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	/*public PokerType getPokerTypeById(int id) {
		if (HOLDEM.getId() == id) {
			return HOLDEM;
		} else if (OMAHA.getId() == id) {
			return OMAHA;
		} else {
			throw new IllegalArgumentException();
		}
	}*/
}
