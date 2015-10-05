package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable, PokerController {
	
	@FXML
	private AnchorPane rootPane;
	
	@FXML
	private Label pokerLabel;

	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Label usernameLabel;
	
	@FXML
	private Label passwordLabel;
	
	@FXML
	private Button loginButton;
	
	private Model model;
	
	private FrameController frameController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new Model();
	}
	
	@FXML protected void loginHandler(ActionEvent event) {
		model.login(usernameField.getText(), passwordField.getText());
	}
	
	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}
	
	public void goToReg() {
		frameController.setRegistrationFXML();
	}

}
