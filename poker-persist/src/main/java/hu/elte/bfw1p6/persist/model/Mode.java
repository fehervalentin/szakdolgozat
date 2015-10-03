package hu.elte.bfw1p6.persist.model;

public class Mode {
	private int id;
	private String name;
	private int maxPlayers;
	private int maxTime;
	private int cardsToHouse;
	private int cardsToPlayers;
	
	public Mode(String name, int maxPlayers, int maxTime, int cardsToHouse, int cardsToPlayers) {
		super();
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.maxTime = maxTime;
		this.cardsToHouse = cardsToHouse;
		this.cardsToPlayers = cardsToPlayers;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	public int getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
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
