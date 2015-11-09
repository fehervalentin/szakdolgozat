package hu.elte.bfw1p6.poker.client.controller.main;

import javafx.fxml.Initializable;

/**
 * Azon interface, melyet a kliens controllerek implementálnak,
 * és ezáltal a controllerek tudnak FXML-t váltani.
 * @author feher
 *
 */
public interface PokerClientController extends Initializable {
	public void setDelegateController(FrameController frameController);
}
