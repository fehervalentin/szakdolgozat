package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class AbstractPokerClientController implements PokerClientController {
	

	/**
	 * A kliens oldali model.
	 */
	protected Model model;

	protected FrameController frameController;

	protected Alert errorAlert;

	protected Alert successAlert;
	
	public AbstractPokerClientController() {
		errorAlert = new Alert(AlertType.ERROR);
		successAlert = new Alert(AlertType.INFORMATION);
	}

	public abstract void initialize(URL location, ResourceBundle resources);

	public abstract void setDelegateController(FrameController frameController);

}
