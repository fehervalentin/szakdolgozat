package hu.elte.bfw1p6.poker.client.controller;

public class HoldemDefaultValues extends AbstractDefaultValues {

	private static HoldemDefaultValues instance = null;

	private HoldemDefaultValues() {
		this.DECK_POINT = new int[]{430, 300};
		this.MY_CARDS_POSITION = new int[]{600, 430};
		this.MY_CARDS_COUNT = 2;
	}

	public synchronized static HoldemDefaultValues getInstance() {
		if (instance == null) {
			instance = new HoldemDefaultValues();
		}
		return instance;
	}
}
