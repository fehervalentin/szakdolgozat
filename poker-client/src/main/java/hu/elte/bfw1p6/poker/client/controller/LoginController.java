package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable, PokerClientController {
	
	@FXML private AnchorPane rootPane;
	@FXML private Label pokerLabel;
	@FXML private Label usernameLabel;
	@FXML private Label passwordLabel;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	@FXML private Button loginButton;
	
	private Model model;
	
	private FrameController frameController;
	
	private Alert alert;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		alert = new Alert(AlertType.ERROR);
		model = Model.getInstance();
		
		usernameField.setText("asd");
		passwordField.setText("asd");
	}
	
	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}
	
	@FXML
	protected void loginHandler(ActionEvent event) {
		try {
			model.login(usernameField.getText(), passwordField.getText());
			frameController.setTableListerFXML();
		} catch (RemoteException | PokerInvalidUserException | PokerDataBaseException e) {
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}
	
	@FXML
	protected void goToReg() {
		frameController.setRegistrationFXML();
	}
}
