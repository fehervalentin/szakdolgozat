package hu.elte.bfw1p6.poker.client.controller;

public class PokerHoldemDefaultValues {

	private static PokerHoldemDefaultValues instance = null;

	/**
	 * A dealer zseton kép URL-je.
	 */
	public final String DEALER_BUTTON_IMAGE_URL = "/images/dealer.png";

	/**
	 * Az alapprofilképek URL-je
	 */
	public final String PROFILE_IMAGE_URL = "/images/profile.png";

	/**
	 * A kártyapakli kép URL-je
	 */
	public final String DECK_IMAGE_URL = "/images/cards/deck.png";

	/**
	 * Lefele fordított kárytaszél
	 * <img src="../../../../../../../resources/images/cards/b1pl.png" />
	 */
	public final String CARD_SIDE_IMAGE_URL = "/images/cards/b1pl.png";
	public final String CARD_BACKFACE_IMAGE= "/images/cards/b1fv.png";


	/* A saját profilképem helye. */
	public final int[] MY_PROFILE_POINT = new int[]{628, 540};

	/* A többi profilkép helye. */
	public final int[] PROFILE_POINTS = new int[]{188, 485, 232, 40, 631, 0, 1031, 45, 1084, 485};
	
	/**
	 * A lefordított kártyalapokat hova kell generálni.
	 */
	public final int[] CARD_B1FV_POINTS = new int[]{293, 400, 322, 200, 640, 155, 940, 180, 1000, 400};
	
	/* A saját kártyalapjaimnak a helye. */
	public final int[] MY_CARDS_POSITION = new int[]{600, 430};

	/**
	 * A kártyapakli elhelyezkedése.
	 */
	public final int[] DECK_POINT = new int[]{430, 300};

	public final int[] DEALER_BUTTON_POSITIONS = new int[]{580, 530, 320, 500, 265, 240, 720, 160, 1020, 200, 970, 505};

	/**
	 * A profilkép magassága és szélessége.
	 */
	public final int PROFILE_SIZE = 80;

	/**
	 * Maximum hány darab játékos lehet egy asztalnál.
	 */
	public final int PROFILE_COUNT = 5;

	/**
	 * A kártyalapok magassága.
	 */
	public final int CARD_HEIGHT = 96;

	/**
	 * A kártyalapok szélessége.
	 */
	public final int CARD_WIDTH = 71;

	/**
	 * A kártyaszél szélessége.
	 */
	public final int CARD_SIDE_WIDTH = 12;
	
	public final int DEALER_BUTTON_SIZE = 40;

	private PokerHoldemDefaultValues() {
	}

	public synchronized static PokerHoldemDefaultValues getInstance() {
		if (instance == null) {
			instance = new PokerHoldemDefaultValues();
		}
		return instance;
	}
}
