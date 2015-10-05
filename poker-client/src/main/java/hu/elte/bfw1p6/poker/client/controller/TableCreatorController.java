package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class TableCreatorController implements Initializable {
	
	@FXML
	private AnchorPane rootPane;
	
	@FXML
	private Label tableNameLabel;
	
	@FXML
	private Label gameType;
	
	@FXML
	private Label maxTimeLable;
	
	@FXML
	private Label maxPlayerLabel;

	@FXML
	private Label limitLabel;
	
	@FXML
	private Label smallBlindLabel;
	
	@FXML
	private Label bigBlindLabel;
	
	@FXML
	private Label typeLabel;
	
	
	
	
	
	@FXML
	private TextField tableNameTextField;
	
	@FXML ComboBox<String> gameTypeComboBox;
	
	@FXML
	private TextField maxTimeField;
	
	@FXML
	private TextField maxPlayerTextField;
	
	@FXML
	private TextField limitTextField;
	
	@FXML
	private TextField smallBlindTextField;
	
	@FXML
	private TextField bigBlindtTextField;
	
	
	
	
	
	
	@FXML
	private Button createTableButton;
	
	@FXML
	private Button backButton;
	
	private Model model;
	
	private FrameController frameController;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String s;
		//model = new Model();
	}
	
	@FXML protected void handleCreateTableButton(ActionEvent event) {
		/*if (model.registration(usernameField.getText(), passwordField.getText())) {
			System.out.println("A regisztráció sikeres!");
		}*/
		//getClass().getClassLoader().getResource("/fxml/Game.fxml")
//		rootPane.getChildren().setAll(FXMLLoader.load(getClass().getClassLoader().getResource("anyad")));
	}
	
	@FXML
	protected void createTableHandler(ActionEvent event) {
		frameController.goToLogin();
	}
	
	@FXML
	protected void backHandler(ActionEvent event) {
		
	}
	
	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}

}
