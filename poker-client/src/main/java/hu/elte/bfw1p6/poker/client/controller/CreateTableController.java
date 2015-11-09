package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Új poker asztal entitást létrehozó controller.
 * @author feher
 *
 */
public class CreateTableController extends AbstractPokerClientController {

	private final String SUCC_CREATE_TABLE_MSG = "A táblát sikeresen létrehoztad!";
	private final String SUCC_MODIFY_TABLE_MSG = "A táblát sikeresen modosítottad!";
	private final String ERR_TEXTFIELD_TEXT = "Hibás szám formátum a %s mezőben!";
	private final String ERR_STYLECLASS = "hiba";

	@FXML private AnchorPane rootPane;
	
	@FXML private ComboBox<String> gameTypeComboBox;

	@FXML private TextField tableNameTextField;

	@FXML private TextField maxTimeField;

	@FXML private TextField maxPlayerTextField;

	@FXML private TextField defaultPotField;

	@FXML private TextField maxBetTextField;

	@FXML private Button createTableButton;

	@FXML private Button backButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		model = Model.getInstance();
		fillPokerTypes();
		dummyData();
		setParamPokerTable();
	}

	private void fillPokerTypes() {
		List<String> pokerTypes = new ArrayList<>();
		for (int i = 0; i < PokerType.values().length; i++) {
			pokerTypes.add(PokerType.values()[i].toString());
		}
		gameTypeComboBox.setItems(FXCollections.observableArrayList(pokerTypes));
	}

	@Deprecated
	private void dummyData() {
		tableNameTextField.setText("próba szerver 1");
		gameTypeComboBox.getSelectionModel().select(0);
		maxTimeField.setText("39");
		maxPlayerTextField.setText("5");
		defaultPotField.setText("12");
		maxBetTextField.setText("100");
	}

	private void setParamPokerTable() {
		PokerTable pokerTable = model.getParamPokerTable();
		if (pokerTable != null) {
			tableNameTextField.setText(pokerTable.getName());
			gameTypeComboBox.getSelectionModel().select(pokerTable.getPokerType().getName());
			maxTimeField.setText(String.valueOf(pokerTable.getMaxTime()));
			maxPlayerTextField.setText(String.valueOf(pokerTable.getMaxPlayers()));
			defaultPotField.setText(pokerTable.getDefaultPot().toString());
			maxBetTextField.setText(pokerTable.getMaxBet().toString());
		}
	}

	@FXML
	protected void createTableHandler(ActionEvent event) {
		maxTimeField.getStyleClass().remove(ERR_STYLECLASS);
		maxPlayerTextField.getStyleClass().remove(ERR_STYLECLASS);
		defaultPotField.getStyleClass().remove(ERR_STYLECLASS);
		maxBetTextField.getStyleClass().remove(ERR_STYLECLASS);

		String tableName = tableNameTextField.getText();

		PokerType pokerType = null;
		Integer maxTime = null;
		Integer maxPlayers = null;
		BigDecimal maxBet = null;
		BigDecimal defaultPot = null;
		try {
			pokerType = PokerType.valueOf(gameTypeComboBox.getSelectionModel().getSelectedItem());
		} catch (IllegalArgumentException ex) {
			showErrorAlertBox("Hibás játék mód!");
			return;
		}
		try {
			maxTime = new Integer(maxTimeField.getText());
		} catch (NumberFormatException ex) {
			maxTimeField.getStyleClass().add(ERR_STYLECLASS);
			showErrorAlertBox(String.format(ERR_TEXTFIELD_TEXT, "gondolkodási idő"));
			return;
		}
		try {
			maxPlayers = Integer.valueOf(maxPlayerTextField.getText());
		} catch (NumberFormatException ex) {
			maxPlayerTextField.getStyleClass().add(ERR_STYLECLASS);
			showErrorAlertBox(String.format(ERR_TEXTFIELD_TEXT, "maximum játékos"));
			return;
		}
		try {
			defaultPot = BigDecimal.valueOf(Double.valueOf(defaultPotField.getText()));
		} catch (NumberFormatException ex) {
			defaultPotField.getStyleClass().add(ERR_STYLECLASS);
			showErrorAlertBox(String.format(ERR_TEXTFIELD_TEXT, "alaptét"));
			return;
		}
		try {
			maxBet = BigDecimal.valueOf(Double.valueOf(maxBetTextField.getText()));
		} catch (NumberFormatException ex) {
			maxBetTextField.getStyleClass().add(ERR_STYLECLASS);
			showErrorAlertBox(String.format(ERR_TEXTFIELD_TEXT, "maximum tét"));
			return;
		}
		
		// nincs/nem volt átadandó paraméter, tehát új táblát kell létrehoznom
		if (model.getParamPokerTable() == null) {
			// ekkor új táblát hozunk létre
			try {
				PokerTable t = new PokerTable(tableName, maxTime, maxPlayers, maxBet, defaultPot, pokerType);
				model.createTable(t);
				showSuccessAlertBox(SUCC_CREATE_TABLE_MSG);
				frameController.setTableListerFXML();
			} catch (RemoteException | PokerDataBaseException e) {
				showErrorAlertBox(e.getMessage());
			} catch (PokerUnauthenticatedException e) {
				showErrorAlertBox(e.getMessage());
				frameController.setLoginFXML();
			}
		} else {
			// különben pedig volt paraméter => módosítunk
			try {
				PokerTable t = model.getParamPokerTable();
				t.setName(tableName);
				t.setMaxTime(maxTime);
				t.setMaxPlayers(maxPlayers);
				t.setMaxPlayers(maxPlayers);
				t.setMaxBet(maxBet);
				t.setDefaultPot(defaultPot);
				t.setPokerType(pokerType);
				model.modifyTable(t);
				showSuccessAlertBox(SUCC_MODIFY_TABLE_MSG);
				frameController.setTableListerFXML();
			} catch (RemoteException | PokerDataBaseException e) {
				showErrorAlertBox(e.getMessage());
			} catch (PokerUnauthenticatedException e) {
				showErrorAlertBox(e.getMessage());
			}
		}
		model.setParameterPokerTable(null);
	}

	@FXML
	protected void backHandler(ActionEvent event) {
		frameController.setTableListerFXML();
	}

	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}
	
	private void showErrorAlertBox(String msg) {
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}
	
	private void showSuccessAlertBox(String msg) {
		successAlert.setContentText(msg);
		successAlert.showAndWait();
	}
}