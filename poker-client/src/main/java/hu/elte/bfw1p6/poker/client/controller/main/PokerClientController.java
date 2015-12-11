package hu.elte.bfw1p6.poker.client.controller.main;

import javafx.fxml.Initializable;

/**
 * Azon interface, melyet a kliens controllerek implementálnak,
 * és ezáltal a controllerek tudnak FXML-t váltani.
 * @author feher
 *
 */
public interface PokerClientController extends Initializable {
	
	/**
	 * A kliens oldali vezérlő rétegbeli objektumok ezen delegált objektum segítségével tudnak másik controllert beállítani.
	 * @param frameController a delegált objektum
	 */
	public void setFrameController(FrameController frameController);
	
	/**
	 * A szerver ezen a metóduson keresztül tudja frissíteni a klienseket.
	 * @param msg a szerver által küldött üzenet
	 */
	public void update(Object msg);
}