package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Regisztrációs controller.
 * @author feher
 *
 */
public class RegistrationController extends AbstractPokerClientController {

	private final String OK_REG_MSG = "Sikeresen regisztráltál!";
	private final String ERR_REG_DIFF_PW = "A két jelszó nem egyezik!";
	private final String ERR_STYLECLASS = "hiba";

	@FXML private Label pokerLabel;
	@FXML private Label registrationLabel;

	@FXML private TextField usernameField;

	@FXML private PasswordField passwordField;
	@FXML private PasswordField rePasswordField;

	@FXML private Button regButton;
	@FXML private Button backButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * A REGISTRATION gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleRegistrationButton(ActionEvent event) {
		passwordField.getStyleClass().remove(ERR_STYLECLASS);
		rePasswordField.getStyleClass().remove(ERR_STYLECLASS);

		if (!passwordField.getText().equals(rePasswordField.getText())) {
			passwordField.getStyleClass().add(ERR_STYLECLASS);
			rePasswordField.getStyleClass().add(ERR_STYLECLASS);
			showErrorAlert(ERR_REG_DIFF_PW);
			return;
		}
		try {
			model.registration(usernameField.getText(), passwordField.getText());
			showSuccessAlert(OK_REG_MSG);
			frameController.setLoginFXML();
		} catch (PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A BACK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void goToLogin(ActionEvent event) {
		frameController.setLoginFXML();
	}
}