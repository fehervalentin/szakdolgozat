package hu.elte.bfw1p6.poker.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hu.elte.bfw1p6.poker.model.Card;
import hu.elte.bfw1p6.poker.model.CardSuit;

/**
 * A kártyapaklit megvalósító osztály.
 * @author feher
 *
 */
public class Deck {
	
	/**
	 * Összesen hány darab különböző kártyalap van a pakliban (jokerek nélkül).
	 */
	private final int NUMER_OF_CARDS = 52;
	
	/**
	 * Kártyák listája.
	 */
	private final List<Card> cards;
	
	/**
	 * Hány lapot húztunk már le a kártyapakli tetejéről.
	 */
	private int stackPointer;
	
	/**
	 * Létrehozza a kártyapaklit.
	 */
	public Deck() {
		cards = new ArrayList<>();
		loadCards();
		reset();
	}
	
	private void loadCards() {
        double temp;
        for (int i = 1; i <= NUMER_OF_CARDS; i++) {
            temp = Math.floor(i / 4.1);
            cards.add(new Card(i, CardSuit.values()[(i % 4)], (int) (14 - temp)));
        }
    }
	
	/**
	 * Húz egy kártyát a kártyapakli tetejéről
	 * @return a húzott kártya
	 */
	public Card popCard() {
		Card card = cards.get(stackPointer);
		++stackPointer;
		return card;
	}
	
	/**
	 * Megkeveri a kártyapaklit
	 */
	public void reset() {
		stackPointer = 0;
		Collections.shuffle(cards, new Random(System.nanoTime()));
	}
}
