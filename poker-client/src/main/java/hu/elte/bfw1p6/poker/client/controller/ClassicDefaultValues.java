package hu.elte.bfw1p6.poker.client.controller;

public class ClassicDefaultValues extends AbstractDefaultValues {
	
	private static ClassicDefaultValues instance = null;

	private ClassicDefaultValues() {
		this.MY_CARDS_POSITION = new int[]{400, 430};
		this.MY_CARDS_COUNT = 5;
	}

	public synchronized static ClassicDefaultValues getInstance() {
		if (instance == null) {
			instance = new ClassicDefaultValues();
		}
		return instance;
	}
}
