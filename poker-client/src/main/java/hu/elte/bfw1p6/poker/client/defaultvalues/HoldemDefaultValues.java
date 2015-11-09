package hu.elte.bfw1p6.poker.client.defaultvalues;

public class HoldemDefaultValues extends AbstractDefaultValues {

	private static HoldemDefaultValues instance = null;

	private HoldemDefaultValues() {
		this.MY_CARDS_POSITION = new int[]{600, 430};
		this.CARD_B1FV_POINTS = new int[]{283, 400, 312, 200, 630, 155, 930, 180, 990, 400};
		this.MY_CARDS_COUNT = 2;
	}

	public synchronized static HoldemDefaultValues getInstance() {
		if (instance == null) {
			instance = new HoldemDefaultValues();
		}
		return instance;
	}
}
