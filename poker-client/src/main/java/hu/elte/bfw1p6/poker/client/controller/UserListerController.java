package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.User;
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
	
	/**
	 * Egy regisztrált felhasználó oszlopai.
	 */
	private final String[] COLUMNS = {"userName", "regDate", "balance", "admin"};
	
	@FXML private Button backButton;
	
	@FXML private TableColumn<User, String> userNameColumn;
	@FXML private TableColumn<User, Long> regDateColumn;
	@FXML private TableColumn<User, BigDecimal> balanceColumn;
	@FXML private TableColumn<User, Boolean> adminColumn;
	
	@FXML private TableView<User> userView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>(COLUMNS[0]));
		regDateColumn.setCellValueFactory(new PropertyValueFactory<User, Long>(COLUMNS[1]));
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

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}
	
	/**
	 * A BACK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}
}