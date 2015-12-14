package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Regisztrált felhasználókat listázó controller.
 * @author feher
 *
 */
public class UserListerController extends AbstractPokerClientController {
	
	private final String ERR_USER_SELECTION = "Nem választottál ki egy felhasználót sem!";
	private final String SUCC_USER_MODIFY = "Sikeresen módosítottad a felhasználót!";
	private final String SUCC_USER_DELETE = "Sikeresen törölted a felhasználót!";
	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private final String INITIAL_BALANCE = "10000.00"; // elegánsabb lenne szervertől elkérni...
	
	/**
	 * Egy regisztrált felhasználó oszlopai.
	 */
	private final String[] COLUMNS = {"userName", "regDate", "balance", "admin"};
	
	@FXML private Button backButton;
	@FXML private Button modifyAdminButton;
	@FXML private Button resetBalanceButton;
	@FXML private Button deleteUserButton;

	@FXML private TableColumn<User, String> userNameColumn;
	@FXML private TableColumn<User, String> regDateColumn;
	@FXML private TableColumn<User, BigDecimal> balanceColumn;
	@FXML private TableColumn<User, Boolean> adminColumn;
	
	@FXML private TableView<User> userView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>(COLUMNS[0]));
		regDateColumn.setCellValueFactory(user -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			property.setValue(dateFormat.format(user.getValue().getRegDate() * 1000));
			return property;
		});
		regDateColumn.setMinWidth(180);
		balanceColumn.setCellValueFactory(new PropertyValueFactory<User, BigDecimal>(COLUMNS[2]));
		adminColumn.setCellValueFactory(new PropertyValueFactory<User, Boolean>(COLUMNS[3]));
		try {
			List<User> players = model.getUsers();
			userView.getItems().setAll(players);
		} catch (PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}
	
	private User getSelectedUser() {
		return userView.getSelectionModel().getSelectedItem();
	}

	@Override
	public void setFrameController(FrameController frameController) {
		this.frameController = frameController;
	}
	
	/**
	 * A DELETE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleDeleteUser(ActionEvent event) {
		User selectedUser = getSelectedUser();
		if (selectedUser != null) {
			try {
				model.deleteUser(selectedUser);
				showSuccessAlert(SUCC_USER_DELETE);
			} catch (PokerDataBaseException e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		} else {
			showErrorAlert(ERR_USER_SELECTION);
		}
	}
	
	/**
	 * A RESET gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleResetBalance(ActionEvent event) {
		User selectedUser = getSelectedUser();
		if (selectedUser != null) {
			try {
				selectedUser.setBalance(new BigDecimal(INITIAL_BALANCE));
				model.modifyUser(selectedUser);
				showSuccessAlert(SUCC_USER_MODIFY);
			} catch (PokerDataBaseException e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		} else {
			showErrorAlert(ERR_USER_SELECTION);
		}
	}
	
	/**
	 * Az ADMIN gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleAdmin(ActionEvent event) {
		User selectedUser = getSelectedUser();
		if (selectedUser != null) {
			try {
				selectedUser.setAdmin(!selectedUser.getAdmin());
				model.modifyUser(selectedUser);
				showSuccessAlert(SUCC_USER_MODIFY);
			} catch (PokerDataBaseException e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		} else {
			showErrorAlert(ERR_USER_SELECTION);
		}
	}
	
	/**
	 * A BACK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}

	@Override
	public void update(Object msg) {
		// TODO Auto-generated method stub
		
	}
}