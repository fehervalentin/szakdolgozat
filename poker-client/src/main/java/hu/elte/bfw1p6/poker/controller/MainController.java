package hu.elte.bfw1p6.poker.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class MainController implements Initializable {
	
	@FXML
	private AnchorPane content;
	
	private Model model;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new Model();
	}
	
	@FXML protected void goToRegistrationHandler(ActionEvent event) {
		content.getChildren().clear();
	   	try {
			content.getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/Registration.fxml")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML protected void goToLoginHandler(ActionEvent event) {
		content.getChildren().clear();
	   	try {
			content.getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/Login.fxml")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
