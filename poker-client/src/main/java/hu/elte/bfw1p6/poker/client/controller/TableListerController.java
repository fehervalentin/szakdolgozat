package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Tábla listázó controller.
 * @author feher
 *
 */
public class TableListerController extends AbstractPokerClientController implements PokerRemoteObserver {

	private final String[] COLUMNS = {"name", "pokerType", "maxTime", "maxPlayers", "defaultPot", "maxBet"};
	
	private final String NO_TABLE_SELECTED_MESSAGE = "Nem választottál ki egy táblát sem!";
	private final String SUCC_TABLE_DELETE_MSG = "Sikeresen kitörölted a táblát!";

	/**
	 * Hálózati kommunikációért felelős controller.
	 */
	private CommunicatorController commCont;

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
	@FXML private Button modifyTableButton;
	@FXML private Button deleteTableButton;
	@FXML private Button profileManagerButton;
	@FXML private Button viewUsersbutton;


	public TableListerController() {
		try {
			commCont = new CommunicatorController(this);
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		try {
			List<PokerTable> tables = model.registerTableViewObserver(commCont);
			tableView.getItems().setAll(tables);
			//TODO: ezt kitörölni
//			tableView.getSelectionModel().select(0);
//			connectButton.fire();
		} catch (PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e ) {
			remoteExceptionHandler();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableName.setCellValueFactory(new PropertyValueFactory<PokerTable, String>(COLUMNS[0]));
		pokerType.setCellValueFactory(new PropertyValueFactory<PokerTable, PokerType>(COLUMNS[1]));
		maxTime.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>(COLUMNS[2]));
		maxPlayers.setCellValueFactory(new PropertyValueFactory<PokerTable, Integer>(COLUMNS[3]));
		defaultPot.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>(COLUMNS[4]));
		maxBet.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>(COLUMNS[5]));

		try {
			if(!model.isAdmin()) {
				createTableButton.setVisible(false);
				modifyTableButton.setVisible(false);
				deleteTableButton.setVisible(false);
				viewUsersbutton.setVisible(false);
			}
		} catch (PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}
	/**
	 * A CONNECT TO TABLE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleConnectToTable() {
		PokerTable selectedPokerTable = getSelectedPokerTable();
		if (selectedPokerTable == null) {
			showErrorAlert(NO_TABLE_SELECTED_MESSAGE);
		} else {
			model.setParameterPokerTable(selectedPokerTable);
			try {
				if (model.canSitIn()) {
					frameController.setMainGameFXML(selectedPokerTable.getPokerType());
				} else {
					showErrorAlert("Nincs szabad hely az asztalnál!");
				}
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		}
	}

	private PokerTable getSelectedPokerTable() {
		return tableView.getSelectionModel().getSelectedItem();
	}

	/**
	 * A CREATE TABLE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleCreateTable(ActionEvent event) {
		frameController.setCreateTableFXML();
	}

	/**
	 * A MODIFY TABLE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleModifyTable(ActionEvent event) {
		PokerTable selectedPokerTable = getSelectedPokerTable();
		if (selectedPokerTable == null) {
			showErrorAlert(NO_TABLE_SELECTED_MESSAGE);
		} else {
			model.setParameterPokerTable(selectedPokerTable);
			frameController.setCreateTableFXML();
		}
	}

	/**
	 * A DELETE TABLE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleDeleteTable(ActionEvent event) {
		PokerTable selectedPokerTable = getSelectedPokerTable();
		if (selectedPokerTable != null) {
			try {
				model.deleteTable(selectedPokerTable);
				showSuccessAlert(SUCC_TABLE_DELETE_MSG);
			} catch (PokerDataBaseException e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		} else {
			showErrorAlert(NO_TABLE_SELECTED_MESSAGE);
		}
	}

	/**
	 * A LOGOUT gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleLogout(ActionEvent event) {
		try {
			model.logout(commCont);
			frameController.setLoginFXML();
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A VIEW USERS gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleViewUsers(ActionEvent event) {
		frameController.setUsersFXML();
	}

	/**
	 * A PROFILE MANAGER gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleProfileManager(ActionEvent event) {
		frameController.setProfileManagerFXML();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Object updateMsg) throws RemoteException {
		if (updateMsg instanceof List<?>) {
			List<PokerTable> tables = (List<PokerTable>)updateMsg;
			tableView.getItems().setAll(tables);
			System.out.println("MEGKAPTAM A TÁBLÁKAT");
		}
	}
}