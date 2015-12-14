package hu.elte.bfw1p6.poker.client.defaultvalues;

import hu.elte.bfw1p6.poker.model.entity.PokerType;

/**
 * A classic játkstílus megjelenítési specifikus alapértékek.
 * @author feher
 *
 */
public class ClassicDefaultValues extends AbstractDefaultValues {
	
	private static ClassicDefaultValues instance = null;

	private ClassicDefaultValues() {
		this.MY_CARDS_POSITION = new int[]{528, 430};
		this.CARD_B1FV_POINTS = new int[]{285, 400, 315, 200, 605, 155, 895, 180, 950, 400};
		this.MY_CARDS_COUNT = PokerType.CLASSIC.getCardsToPlayers();
	}

	public synchronized static ClassicDefaultValues getInstance() {
		if (instance == null) {
			instance = new ClassicDefaultValues();
		}
		return instance;
	}
}