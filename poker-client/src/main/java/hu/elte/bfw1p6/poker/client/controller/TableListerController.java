package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.observer.controller.PokerRemoteObserverTableViewController;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableListerController implements PokerClientController, PokerRemoteObserverTableViewController, Initializable {
	
	private final String NO_TABLE_SELECTED_MESSAGE = "Nem választottál ki egy táblát sem!";

	private FrameController frameController;

	@FXML private TableView<PokerTable> tableView;
	@FXML private TableColumn<PokerTable, String> tableName;
	@FXML private TableColumn<PokerTable, PokerType> pokerType;
	@FXML private TableColumn<PokerTable, Integer> maxTime;
	@FXML private TableColumn<PokerTable, Integer> maxPlayers;
	@FXML private TableColumn<PokerTable, BigDecimal> defaultPot;
	@FXML private TableColumn<PokerTable, BigDecimal> maxBet;
	@FXML private Button connectButton;
	@FXML private Button createTableButton;
	
	private Alert alert;
	
	private Model model;
	
	public TableListerController() {
		model = Model.getInstance();
		alert = new Alert(AlertType.ERROR);
		alert.setContentText(NO_TABLE_SELECTED_MESSAGE);
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			model.registerObserver(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableName.setCellValueFactory(new PropertyValueFactory<PokerTable, String>("name"));
		pokerType.setCellValueFactory(new PropertyValueFactory<PokerTable, PokerType>("pokerType"));
		maxTime.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>("maxTime"));
		maxPlayers.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>("maxPlayers"));
		defaultPot.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>("defaultPot"));
		maxBet.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>("maxBet"));
	}

	@FXML
	protected void handleConnectToTable() {
		PokerTable table = tableView.getSelectionModel().getSelectedItem();
		if (table == null) {
			alert.showAndWait();
		} else {
			ConnectTableHelper.getInstance().setPokerTable(table);
			frameController.setMainGameFXML();
		}
	}
	
	@FXML
	protected void handleCreateTable() {
		frameController.setCreateTableFXML();
	}

	@Override
	public void updateTableView(List<PokerTable> tables) {
		tableView.getItems().setAll(tables);
	}
}
