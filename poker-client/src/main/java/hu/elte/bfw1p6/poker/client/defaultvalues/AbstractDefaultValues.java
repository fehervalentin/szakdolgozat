package hu.elte.bfw1p6.poker.client.defaultvalues;

/**
 * A kliens oldali megjelenítéshez szükséges beégetett alapértékek.
 * @author feher
 *
 */
public class AbstractDefaultValues {
	
	/**
	 * A stílusleíró URL-je.
	 */
	public final String CSS_PATH = "styles/poker.css";
	
	/**
	 * Az alkalmazás neve.
	 */
	public final String APP_NAME = "Poker";
	
	/**
	 * A képek kiterjesztése.
	 */
	public final String PICTURE_EXTENSION = ".png";
	
	/**
	 * A tábla közepén elhelyezkedő kártyalapok pozíciói.
	 */
	public final int[] MIDDLE_CARD_POINT = {525, 300, 601, 300, 677, 300, 753, 300, 829, 300};
//	public final int[] MIDDLE_CARD_POINT = {457, 300, 533, 300, 609, 300, 685, 300, 761, 300};
	
	/**
	 * A GUI-n objektumok megjelölésére szolgáló styleclass.
	 */
	public String MARKER_STYLECLASS = "glow";
	
	/**
	 * A classic játékstílus GUI részén a cserére kiválasztott kártyákat jelöli meg.
	 */
	public String SELECTED_CARD_MAKER_STYLECLASS = "selected";
	
	/**
	 * A kártyalapjaim pozíciói.
	 */
	public int[] MY_CARDS_POSITION;
	
	/**
	 * A kártyalapjaim darabszáma.
	 */
	public int MY_CARDS_COUNT;
	
	/**
	 * Az összes kártyalap darabszáma.
	 */
	public int CARDS_COUNT = 52;
	
	/**
	 * A lefordított kártyalapokat hova kell generálni.
	 */
	public int[] CARD_B1FV_POINTS;
	
	/**
	 * A kártyapakli helye.
	 */
	public int[] DECK_POINT = new int[]{430, 300};
	
	/**
	 * A kártyák elérési útvonala.
	 */
	public final String CARD_IMAGE_PREFIX = "/images/cards/";
	
	/**
	 * A chipek elérési útvonala.
	 */
	public final String CHIP_IMAGE_PREFIX = "/images/chips/";

	/**
	 * A dealer zseton kép URL-je.
	 */
	public final String DEALER_BUTTON_IMAGE_URL = "/images/dealer" + PICTURE_EXTENSION;

	/**
	 * Az alapprofilképek URL-je.
	 */
	public final String PROFILE_IMAGE_URL = "/images/profile" + PICTURE_EXTENSION;

	/**
	 * A kártyapakli kép URL-je.
	 */
	public final String DECK_IMAGE_URL = "/images/cards/deck" + PICTURE_EXTENSION;
	

	/**
	 * Lefele fordított kártyaszél kép URL-je.
	 */
	public final String CARD_SIDE_IMAGE_URL = "/images/cards/b1pl" + PICTURE_EXTENSION;
	
	/**
	 * Lefelé fordított egész kártya kép URL-je.
	 */
	public final String CARD_BACKFACE_IMAGE= "/images/cards/b1fv" + PICTURE_EXTENSION;
	
	/**
	 * A dealer gomb helyei.
	 */
	public final int[] DEALER_BUTTON_POSITIONS = new int[]{580, 530, 320, 500, 265, 240, 720, 160, 1020, 200, 970, 505};
	
	/**
	 * A zsetonok generálási középpontja.
	 */
	public final int[] CHIPS_POINT = new int[]{485, 465};
	
	/**
	 * A zsetonok random "szórása".
	 */
	public final int CHIPS_EPSILON = 20;
	
	/**
	 * A zsetonok mérete.
	 */
	public final int CHIPS_SIZE = 20;
	
	/**
	 * A profilképek helyei.
	 */
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
	
	/**
	 * A dealer gomb mérete.
	 */
	public final int DEALER_BUTTON_SIZE = 40;
}