package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CreateTableController implements Initializable, PokerController {

	@FXML
	private AnchorPane rootPane;

	@FXML
	private Label tableNameLabel;

	@FXML
	private Label gameType;

	@FXML
	private Label maxTimeLable;

	@FXML
	private Label maxPlayerLabel;

	@FXML
	private Label maxBetLabel;

	@FXML
	private Label smallBlindLabel;

	@FXML
	private Label bigBlindLabel;

	@FXML
	private Label typeLabel;





	@FXML
	private TextField tableNameTextField;

	@FXML ComboBox<String> gameTypeComboBox;

	@FXML
	private TextField maxTimeField;

	@FXML
	private TextField maxPlayerTextField;

	@FXML
	private TextField maxBetTextField;

	@FXML
	private TextField smallBlindTextField;

	@FXML
	private TextField bigBlindtTextField;






	@FXML
	private Button createTableButton;

	@FXML
	private Button backButton;

	private Model model;

	private FrameController frameController;
	
	private Alert alert;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = Model.getInstance();
		alert = new Alert(AlertType.ERROR);
	}

//	@FXML protected void handleCreateTableButton(ActionEvent event) {
//		System.out.println("lol");
//		PTable table = new PTable(tableNameLabel.getText(),
//				Integer.valueOf(maxTimeField.getText()),
//				Integer.valueOf(maxPlayerTextField.getText()),
//				BigDecimal.valueOf(Double.valueOf(limitTextField.getText())),
//				BigDecimal.valueOf(Double.valueOf(smallBlindTextField.getText())),
//				BigDecimal.valueOf(Double.valueOf(bigBlindtTextField.getText())));
//		PTable table = new PTable("asd",
//				5,
//				2,
//				new BigDecimal(2),
//				new BigDecimal(2),
//				new BigDecimal(2));
//		table.setType(Type.HOLDEM);
//		try {
//			model.createTable(table);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		List<PTable> tables = new ArrayList<>();//model.getTables();
//		tables.add(table);
//		System.out.println(tables);
//		System.out.println("vege");
//	}

	@FXML
	protected void createTableHandler(ActionEvent event) {
		String tableName = tableNameTextField.getText();
		Integer maxTime = Integer.valueOf(maxTimeField.getText());
		Integer maxPlayers = Integer.valueOf(maxPlayerTextField.getText());
		BigDecimal maxBet = BigDecimal.valueOf(Double.valueOf(maxBetTextField.getText()));
		BigDecimal smallBlind = BigDecimal.valueOf(Double.valueOf(smallBlindTextField.getText()));
		BigDecimal bigBlind = BigDecimal.valueOf(Double.valueOf(bigBlindtTextField.getText()));
		String typeString = gameTypeComboBox.getSelectionModel().getSelectedItem();
		
		PTable t = new PTable(tableName, maxTime, maxPlayers, maxBet, smallBlind, bigBlind);
		
		if (typeString.equals("OMAHA")) {
			t.setType(PokerType.OMAHA);
		} else if (typeString.equals("HOLDEM")) {
			t.setType(PokerType.HOLDEM);
		} else {
			alert.setContentText("Nem megfelelő játék típus lett kiválasztva!");
			alert.showAndWait();
			return;
//			throw new PokerInvalidGameTypeException("Nem megfelelő játék típus!");
		}
		
		try {
			model.createTable(t);
		} catch (RemoteException | PokerInvalidUserException e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void backHandler(ActionEvent event) {
		frameController.setTableListerFXML();
	}

	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}

}
