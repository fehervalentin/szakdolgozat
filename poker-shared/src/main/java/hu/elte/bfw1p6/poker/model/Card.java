package hu.elte.bfw1p6.poker.model;

public class Card {
    private final int id;
    private final int suit;
    private final int value;
    
    public Card(int id, int suit, int value) {
        this.id = id;
        this.suit = suit;
        this.value = value;
    }
    
    public int getSuit() {
        return suit;
    }
    
    public int getValue() {
        return value;
    }
    
    public int getId() {
        return id;
    }
}
