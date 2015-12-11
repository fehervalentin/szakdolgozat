package hu.elte.bfw1p6.poker.client.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Bjelentkezési controller.
 * @author feher
 *
 */
public class LoginController extends AbstractPokerClientController {
	
	@FXML private Label pokerLabel;
	@FXML private Label usernameLabel;
	@FXML private Label passwordLabel;
	
	@FXML private TextField usernameField;
	
	@FXML private PasswordField passwordField;
	
	@FXML private Button loginButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usernameField.setText("admin");
		passwordField.setText("admin");
	}

	/**
	 * A LOGIN gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void loginHandler(ActionEvent event) {
		try {
			model.login(usernameField.getText(), passwordField.getText());
			frameController.setTableListerFXML();
		} catch (PokerInvalidUserException | PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			remoteExceptionHandler();
		}
	}
	
	/**
	 * A REGISTRATION gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void navigateToRegistrationForm(ActionEvent event) {
		frameController.setRegistrationFXML();
	}

	@Override
	public void update(Object msg) {
		// TODO Auto-generated method stub
		
	}
}