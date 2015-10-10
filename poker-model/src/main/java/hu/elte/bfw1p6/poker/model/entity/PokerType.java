package hu.elte.bfw1p6.poker.model.entity;

public enum PokerType {
	
	HOLDEM("HOLDEM", 5, 2),
	OMAHA("OMAHA", 5, 2);
	
	private String name;
	private int cardsToHouse;
	private int cardsToPlayers;

	private PokerType(String name, int cardsToHouse, int cardsToPlayers) {
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
}
