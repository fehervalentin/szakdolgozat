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
		errorAlert = new Alert(AlertType.ERROR);
		successAlert = new Alert(AlertType.INFORMATION);
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
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}
	
	/**
	 * Hibás szerver-kliens kommunikációt kezelő eljárás.
	 */
	public void remoteExceptionHandler() {
//		try {
//			Model.getInstance().setWasError(true);
//		} catch (MalformedURLException | RemoteException | NotBoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}
	
	/**
	 * Információs üzenet megjelenítése.
	 * @param msg az üzenet
	 */
	public void showSuccessAlert(String msg) {
		successAlert.setContentText(msg);
		successAlert.showAndWait();
	}
}