package hu.elte.bfw1p6.poker.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable {
	
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new Model();
	}
	
	@FXML protected void handleLoginButton(ActionEvent event) {
		model.login(usernameField.getText(), passwordField.getText());
		//getClass().getClassLoader().getResource("/fxml/Game.fxml")
//		rootPane.getChildren().setAll(FXMLLoader.load(getClass().getClassLoader().getResource("anyad")));
	}

}
