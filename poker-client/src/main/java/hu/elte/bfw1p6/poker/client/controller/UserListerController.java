package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserListerController implements PokerClientController {
	
	@FXML private Button backButton;
	
	@FXML private TableColumn<PokerPlayer, String> userName;
	@FXML private TableColumn<PokerPlayer, Long> regDate;
	@FXML private TableColumn<PokerPlayer, BigDecimal> balance;
	
	@FXML private TableView<PokerPlayer> userView;
	
	private FrameController frameController;
	
	private Model model;
	
	private Alert alert;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.model = Model.getInstance();
		this.alert = new Alert(AlertType.ERROR);
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}
}
