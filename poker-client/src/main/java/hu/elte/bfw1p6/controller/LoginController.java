package hu.elte.bfw1p6.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.model.LoginUser;
import hu.elte.bfw1p6.model.Model;
import hu.elte.bfw1p6.model.entity.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {
	
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new Model();
	}
	
	@FXML protected void handleLoginButton(ActionEvent event) {
		Player loggedPlayer = null;
		try {
			loggedPlayer = model.login(usernameField.getText(), passwordField.getText());
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(usernameField.getText());
		// TODO szerver publikus interfacet meghívni
	}

}
