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
import hu.elte.bfw1p6.poker.exception.PokerTableDeleteException;
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
	
	private final String ERR_TABLE_SELECTION = "Nem választottál ki egy táblát sem!";
	private final String SUCC_TABLE_DELETE = "Sikeresen kitörölted a táblát!";
	private final String ERR_TABLE_FULL = "Nincs szabad hely az asztalnál!";

	/**
	 * Egy pókerasztal oszlopai.
	 */
	private final String[] COLUMNS = {"name", "pokerType", "maxTime", "maxPlayers", "bigBlind"};
	
	/**
	 * Hálózati kommunikációért felelős controller.
	 */
	private CommunicatorController commCont;

	@FXML private TableView<PokerTable> tableView;

	@FXML private TableColumn<PokerTable, String> tableName;
	@FXML private TableColumn<PokerTable, PokerType> pokerType;
	@FXML private TableColumn<PokerTable, Integer> maxTime;
	@FXML private TableColumn<PokerTable, Integer> maxPlayers;
	@FXML private TableColumn<PokerTable, BigDecimal> bigBlind;

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
			if (tables != null) {
				tableView.getItems().setAll(tables);
			}
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
		bigBlind.setCellValueFactory(new PropertyValueFactory<PokerTable, BigDecimal>(COLUMNS[4]));

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
	@FXML protected void handleConnectToTable(ActionEvent event) {
		PokerTable selectedPokerTable = getSelectedPokerTable();
		if (selectedPokerTable == null) {
			showErrorAlert(ERR_TABLE_SELECTION);
		} else {
			model.setParameterPokerTable(selectedPokerTable);
			try {
				if (model.canSitIn()) {
					frameController.setMainGameFXML(selectedPokerTable.getPokerType());
				} else {
					showErrorAlert(ERR_TABLE_FULL);
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
			showErrorAlert(ERR_TABLE_SELECTION);
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
				showSuccessAlert(SUCC_TABLE_DELETE);
			} catch (PokerDataBaseException | PokerTableDeleteException e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		} else {
			showErrorAlert(ERR_TABLE_SELECTION);
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
			tableView.getItems().setAll((List<PokerTable>)updateMsg);
		}
	}
}