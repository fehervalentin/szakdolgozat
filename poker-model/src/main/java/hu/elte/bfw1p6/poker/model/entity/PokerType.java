package hu.elte.bfw1p6.poker.model.entity;

import javax.persistence.Entity;

@Entity
public enum PokerType {
	
	HOLDEM(1, "Hold'em", 5, 2),
	OMAHA(2, "Omaha", 5, 2);
	
	private long id;
	private String name;
	private int cardsToHouse;
	private int cardsToPlayers;

	private PokerType(long id, String name, int cardsToHouse, int cardsToPlayers) {
		this.name = name;
		this.cardsToHouse = cardsToHouse;
		this.cardsToPlayers = cardsToPlayers;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
