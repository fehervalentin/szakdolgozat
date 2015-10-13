package hu.elte.bfw1p6.poker.client.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.exception.PokerInvalidSession;
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

public class TableListerController implements PokerClientController, Initializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	@FXML private Button logoutButton;

	private transient Alert alert;

	//	private TableViewObserverImpl observer;

	private Model model;

	public TableListerController() {
		model = Model.getInstance();
		alert = new Alert(AlertType.ERROR);
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		try {
			List<PokerTable> tables = model.registerTableViewObserver(frameController);
			tableView.getItems().setAll(tables);
			//			model.registerObserver(frameController);
			//			model.addObserver(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tableName.setCellValueFactory(new PropertyValueFactory<PokerTable, String>("name"));
		pokerType.setCellValueFactory(new PropertyValueFactory<PokerTable, PokerType>("pokerType"));
		maxTime.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>("maxTime"));
		maxPlayers.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>("maxPlayers"));
		defaultPot.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>("defaultPot"));
		maxBet.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>("maxBet"));

		//		observer = new TableViewObserverImpl(this);
	}

	@FXML
	protected void handleConnectToTable() {
		PokerTable table = tableView.getSelectionModel().getSelectedItem();
		if (table == null) {
			alert.setContentText(NO_TABLE_SELECTED_MESSAGE);
			alert.showAndWait();
		} else {
			ConnectTableHelper.getInstance().setPokerTable(table);
			removeObserver();
			frameController.setMainGameFXML();
		}
	}

	@FXML
	protected void handleCreateTable() {
		removeObserver();
		frameController.setCreateTableFXML();
	}

	@FXML
	protected void handleLogout() {
		try {
			model.logout();
			frameController.setLoginFXML();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PokerInvalidSession e) {
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}

	private void removeObserver() {
		try {
			model.removeTableViewObserver(frameController);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateTableView(List<PokerTable> tables) {
		List<PokerTable> valami = model.getTables();
		System.out.println(valami.size());
		tableView.getItems().setAll(valami);
		System.out.println("frissiteni kell a tablazatot!");
		tableView.getItems().setAll(tables);
		System.out.println(tables.size());
		tableView.getColumns().get(0).setVisible(false);
		tableView.getColumns().get(0).setVisible(true);
		//		tableView.setVisible(false);
		//		tableView.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valamivan(Object updateMsg) {
		List<PokerTable> tables = (List<PokerTable>)updateMsg;
		tableView.getItems().setAll(tables);
		System.out.println("TableListerController");
		System.out.println("MEGKAPTAM A TÁBLÁKAT");
	}
}
