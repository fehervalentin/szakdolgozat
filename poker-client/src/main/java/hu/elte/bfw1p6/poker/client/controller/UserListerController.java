package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
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
	
	@FXML private Button backButton;
	
	@FXML private TableColumn<PokerPlayer, String> userName;
	@FXML private TableColumn<PokerPlayer, Long> regDate;
	@FXML private TableColumn<PokerPlayer, BigDecimal> balance;
	
	@FXML private TableView<PokerPlayer> userView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userName.setCellValueFactory(new PropertyValueFactory<PokerPlayer, String>("userName"));
		regDate.setCellValueFactory(new PropertyValueFactory<PokerPlayer, Long>("regDate"));
		balance.setCellValueFactory(new PropertyValueFactory<PokerPlayer, BigDecimal>("balance"));
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		
		List<PokerPlayer> tables;
		try {
			tables = model.getUsers();
			userView.getItems().setAll(tables);
		} catch (PokerDataBaseException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
		
	}
	
	/**
	 * A BACK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}
}
