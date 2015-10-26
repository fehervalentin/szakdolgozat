package hu.elte.bfw1p6.poker.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardRankEnum;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

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
        int suitsLenght = CardSuit.values().length;
        for (int i = 0; i < NUMER_OF_CARDS / suitsLenght; i++) {
        	for (int j = 0; j < suitsLenght; j++) {
        		cards.add(new Card(CardSuitEnum.values()[j], CardRankEnum.values()[i]));
        	}
        }
//        System.out.println(this);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Deck:[" + System.lineSeparator());
		for (int i = 0; i < NUMER_OF_CARDS; i++) {
			sb.append(cards.get(i).toString());
			sb.append(System.lineSeparator());
		}
		sb.append("]");
		return sb.toString();
	}
	
	
}
