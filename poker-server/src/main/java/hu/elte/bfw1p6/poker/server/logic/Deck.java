package hu.elte.bfw1p6.poker.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardRankEnum;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

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
	 * Hányféle kártyatípus van a pakliban.
	 */
	private final int NUMBER_OF_SUITS = 4;
	
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
        for (int i = 0; i < NUMER_OF_CARDS / NUMBER_OF_SUITS; i++) {
        	for (int j = 0; j < NUMBER_OF_SUITS; j++) {
        		cards.add(new Card(CardSuitEnum.values()[j], CardRankEnum.values()[i]));
        	}
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