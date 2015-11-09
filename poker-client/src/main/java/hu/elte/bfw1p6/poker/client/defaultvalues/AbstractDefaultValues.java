package hu.elte.bfw1p6.poker.client.defaultvalues;

public class AbstractDefaultValues {
	
	public int[] MY_CARDS_POSITION;
	
	public int MY_CARDS_COUNT;
	
	/**
	 * A lefordított kártyalapokat hova kell generálni.
	 */
	public int[] CARD_B1FV_POINTS;
	
	public int[] DECK_POINT = new int[]{430, 300};
	
	public final String CARD_IMAGE_PREFIX = "/images/cards/";
	
	public final String CHIP_IMAGE_PREFIX = "/images/chips/";

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
	 * Lefele fordított kártyaszél
	 * <img src="../../../../../../../resources/images/cards/b1pl.png" />
	 */
	public final String CARD_SIDE_IMAGE_URL = "/images/cards/b1pl.png";
	public final String CARD_BACKFACE_IMAGE= "/images/cards/b1fv.png";
	
	public final int[] DEALER_BUTTON_POSITIONS = new int[]{580, 530, 320, 500, 265, 240, 720, 160, 1020, 200, 970, 505};
	
	public final int[] CHIPS_POINT = new int[]{485, 465};
	
	public final int CHIPS_EPSILON = 20;
	
	public final int CHIPS_SIZE = 20;
	
	/* A többi profilkép helye. */
	public final int[] PROFILE_POINTS = new int[]{628, 540, 188, 485, 232, 40, 631, 0, 1031, 45, 1084, 485};
	
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
	
}
