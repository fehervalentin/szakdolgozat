package hu.elte.bfw1p6.poker.client.defaultvalues;

public class ClassicDefaultValues extends AbstractDefaultValues {
	
	private static ClassicDefaultValues instance = null;

	private ClassicDefaultValues() {
		this.MY_CARDS_POSITION = new int[]{488, 430};
		this.CARD_B1FV_POINTS = new int[]{285, 400, 315, 200, 605, 155, 895, 180, 950, 400};
		this.MY_CARDS_COUNT = 5;
	}

	public synchronized static ClassicDefaultValues getInstance() {
		if (instance == null) {
			instance = new ClassicDefaultValues();
		}
		return instance;
	}
}
