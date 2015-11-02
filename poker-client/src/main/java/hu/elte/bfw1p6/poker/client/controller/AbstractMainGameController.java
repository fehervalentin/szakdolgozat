package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerObserverController;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

public abstract class AbstractMainGameController<T extends PokerCommandType<T>> implements Initializable, PokerClientController, PokerObserverController {

	protected FrameController frameController;
	
	protected CommunicatorController commController;

	protected Scene scene;

	protected Alert errorAlert;
	
	public abstract void updateMe(Object updateMsg);

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		this.scene = this.frameController.getScene();
	}
	
	public abstract void initialize(URL location, ResourceBundle resources);

}
