package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CreateTableController implements Initializable, PokerClientController {

	private final String SUCC_CREATE_TABLE_MSG = "A táblát sikeresen létrehoztad!";
	
	@FXML
	private AnchorPane rootPane;

	@FXML
	private TextField tableNameTextField;

	@FXML ComboBox<String> gameTypeComboBox;

	@FXML
	private TextField maxTimeField;

	@FXML
	private TextField maxPlayerTextField;

	@FXML
	private TextField maxBetTextField;

	@FXML
	private TextField defaultPotField;

	@FXML
	private Button createTableButton;

	@FXML
	private Button backButton;

	private Model model;

	private FrameController frameController;
	
	private Alert errorAlert;
	
	private Alert successAlert;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = Model.getInstance();
		errorAlert = new Alert(AlertType.ERROR);
		successAlert = new Alert(AlertType.INFORMATION);
		successAlert.setContentText(SUCC_CREATE_TABLE_MSG);
		tableNameTextField.setText("próba szerver 1");
		gameTypeComboBox.getSelectionModel().select(0);
		maxTimeField.setText("39");
		maxPlayerTextField.setText("5");
		defaultPotField.setText("12");
		maxBetTextField.setText("100");
	}

	@FXML
	protected void createTableHandler(ActionEvent event) {
		//TODO: erősen le kell vizsgálni minden paraméter helyességét!
		String tableName = tableNameTextField.getText();
		Integer maxTime = Integer.valueOf(maxTimeField.getText());
		Integer maxPlayers = Integer.valueOf(maxPlayerTextField.getText());
		BigDecimal maxBet = BigDecimal.valueOf(Double.valueOf(maxBetTextField.getText()));
		BigDecimal defaultPot = BigDecimal.valueOf(Double.valueOf(defaultPotField.getText()));
//		String typeString = gameTypeComboBox.getSelectionModel().getSelectedItem();
		PokerType pokerType = PokerType.valueOf(gameTypeComboBox.getSelectionModel().getSelectedItem());
		
		/*if (typeString.equals("OMAHA")) {
			pokerType = PokerType.OMAHA;
		} else if (typeString.equals("HOLDEM")) {
			pokerType = PokerType.HOLDEM;
		} else {
			alert.setContentText("Nem megfelelő játék típus lett kiválasztva!");
			alert.showAndWait();
			return;
//			throw new PokerInvalidGameTypeException("Nem megfelelő játék típus!");
		}*/
		
		PokerTable t = new PokerTable(tableName, maxTime, maxPlayers, maxBet, defaultPot, pokerType);
		
		
		
		try {
			model.createTable(t);
			successAlert.showAndWait();
			frameController.setTableListerFXML();
		} catch (RemoteException | PokerInvalidUserException | SQLException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@FXML
	protected void backHandler(ActionEvent event) {
		frameController.setTableListerFXML();
	}

	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}

}
