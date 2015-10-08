package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class RegistrationController implements Initializable, PokerClientController {
	
	private final String REG_OK_MSG = "Sikeresen regisztráltál!";
	
	@FXML
	private AnchorPane rootPane;
	
	@FXML
	private Label pokerLabel;
	
	@FXML
	private Label registrationLabel;

	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private PasswordField rePasswordField;
	
	@FXML
	private Label usernameLabel;
	
	@FXML
	private Label passwordLabel;
	
	@FXML
	private Label rePasswordLabel;
	
	@FXML
	private Button regButton;
	
	@FXML
	private Button backButton;
	
	private Model model;
	
	private FrameController frameController;
	
	private Alert alert;
	
	private Alert alertError;
	
	public RegistrationController() {
		alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(REG_OK_MSG);
		
		alertError = new Alert(AlertType.ERROR);
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = Model.getInstance();
	}
	
	@FXML protected void handleRegistrationButton(ActionEvent event) {
		try {
			model.registration(usernameField.getText(), passwordField.getText());
			if (alert.showAndWait().get() == ButtonType.OK) {
				frameController.setLoginFXML();
			}
		} catch (RemoteException e) {
			alertError.showAndWait();
		}
	}
	
	@FXML protected void goToLogin(ActionEvent event) {
		frameController.setLoginFXML();
	}
	
	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}

}
