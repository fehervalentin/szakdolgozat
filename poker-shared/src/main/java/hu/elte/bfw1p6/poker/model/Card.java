package hu.elte.bfw1p6.poker.model;

import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int id;
	private final CardSuit suit;
	private final int value;

	public Card(int id, CardSuit suit, int value) {
		this.id = id;
		this.suit = suit;
		this.value = value;
	}

	public CardSuit getSuit() {
		return suit;
	}

	public int getValue() {
		return value;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "card[suit: " + this.suit.name() + " value: " + this.value + "]";
	}
	
	
}
