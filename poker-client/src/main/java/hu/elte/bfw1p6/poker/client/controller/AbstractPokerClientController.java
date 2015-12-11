package hu.elte.bfw1p6.poker.client.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

/**
 * A póker játék kliens oldali controllerek absztrakciója.
 * @author feher
 *
 */
public abstract class AbstractPokerClientController implements PokerClientController {
	
	private final String ERR_CONN = "Kommunikációs hiba a szerverrel!";
	
	private final String ERR_POPUP_TITLE = "Hiba";
	private final String SUCC_POPUP_TITLE = "Információ";
	
	@FXML private AnchorPane rootPane;

	/**
	 * A kliens oldali model.
	 */
	protected Model model;

	/**
	 * Képernyőképek közötti váltásért felelős controller.
	 */
	protected FrameController frameController;

	/**
	 * Hibaüzenet megjelenítésére szolgáló felugró ablak.
	 */
	private Alert errorAlert;

	/**
	 * Sikeres interakciók visszajelzésére szolgáló felugró ablak.
	 */
	private Alert successAlert;
	
	public AbstractPokerClientController() {
		try {
			if (!(this instanceof ConnectorController)) {
				model = Model.getInstance();
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			remoteExceptionHandler();
		}
	}

	public abstract void initialize(URL location, ResourceBundle resources);

	@Override
	public void setFrameController(FrameController frameController) {
		this.frameController = frameController;
	}
	
	/**
	 * Hibás szerver-kliens kommunikációt kezelő eljárás.
	 */
	public void remoteExceptionHandler() {
		showErrorAlert(ERR_CONN);
		if (!(this instanceof ConnectorController)) {
			frameController.setConnectorFXML();
		}
	}
	
	/**
	 * Hibaüzenetek megjelenítése.
	 * @param msg az üzenet
	 */
	public void showErrorAlert(String msg) {
		if (errorAlert == null) {
			errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText(ERR_POPUP_TITLE);
			errorAlert.setTitle(ERR_POPUP_TITLE);
		}
		// különben nem fér ki a teljes szöveg
		if (msg.contains("Érték hiba")) {
			errorAlert.setWidth(500);
		} else {
			errorAlert.setWidth(367);
		}
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}
	
	/**
	 * Információs üzenet megjelenítése.
	 * @param msg az üzenet
	 */
	public void showSuccessAlert(String msg) {
		if (successAlert == null) {
			successAlert = new Alert(AlertType.INFORMATION);
			successAlert.setHeaderText(SUCC_POPUP_TITLE);
			successAlert.setTitle(SUCC_POPUP_TITLE);
		}
		successAlert.setContentText(msg);
		successAlert.showAndWait();
	}
}