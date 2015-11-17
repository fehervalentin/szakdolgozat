package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
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
	private final String[] COLUMNS = {"userName", "regDate", "balance"};
	
	@FXML private Button backButton;
	
	@FXML private TableColumn<PokerPlayer, String> userName;
	@FXML private TableColumn<PokerPlayer, Long> regDate;
	@FXML private TableColumn<PokerPlayer, BigDecimal> balance;
	
	@FXML private TableView<PokerPlayer> userView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userName.setCellValueFactory(new PropertyValueFactory<PokerPlayer, String>(COLUMNS[0]));
		regDate.setCellValueFactory(new PropertyValueFactory<PokerPlayer, Long>(COLUMNS[1]));
		balance.setCellValueFactory(new PropertyValueFactory<PokerPlayer, BigDecimal>(COLUMNS[2]));
		try {
			userView.getItems().setAll(model.getUsers());
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