package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableListerController implements PokerController, Initializable {
	
	private final String NO_TABLE_SELECTED_MESSAGE = "Nem választottál ki egy táblát sem!";

	private FrameController frameController;

	@FXML private TableView<PTable> tableView;
	@FXML private TableColumn<PTable, String> tableName;
	@FXML private TableColumn<PTable, PokerType> pokerType;
	@FXML private TableColumn<PTable, Integer> maxTime;
	@FXML private TableColumn<PTable, Integer> maxPlayers;
	@FXML private TableColumn<PTable, BigDecimal> maxBet;
	@FXML private TableColumn<PTable, BigDecimal> smallBlind;
	@FXML private TableColumn<PTable, BigDecimal> bigBlind;
	@FXML private Button connectButton;
	@FXML private Button createTableButton;
	
	private Alert alert;


	public TableListerController() {
		//		addTable();
		alert = new Alert(AlertType.ERROR);
		alert.setContentText(NO_TABLE_SELECTED_MESSAGE);
		//akarmi();
	}

	protected void addTable() {
		PTable table = new PTable("asd",
				5,
				2,
				new BigDecimal(2),
				new BigDecimal(2),
				new BigDecimal(2),
				PokerType.HOLDEM);
		ObservableList<PTable> data = tableView.getItems();
		data.add(table);
	}

	private List<PTable> parseUserList(){
		PTable table = new PTable("asd",
				5,
				2,
				new BigDecimal(3),
				new BigDecimal(4),
				new BigDecimal(5),
				PokerType.HOLDEM);

		PTable table2 = new PTable("asd",
				5,
				2,
				new BigDecimal(3),
				new BigDecimal(4),
				new BigDecimal(5),
				PokerType.OMAHA);
		List<PTable> anyad = new ArrayList<>();
		anyad.add(table);
		anyad.add(table2);
		return anyad;
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableName.setCellValueFactory(new PropertyValueFactory<PTable, String>("name"));
		pokerType.setCellValueFactory(new PropertyValueFactory<PTable, PokerType>("pokerType"));
		maxTime.setCellValueFactory(new PropertyValueFactory<PTable, Integer>("maxTime"));
		maxPlayers.setCellValueFactory(new PropertyValueFactory<PTable, Integer>("maxPlayers"));
		maxBet.setCellValueFactory(new PropertyValueFactory<PTable, BigDecimal>("maxBet"));
		smallBlind.setCellValueFactory(new PropertyValueFactory<PTable, BigDecimal>("smallBlind"));
		bigBlind.setCellValueFactory(new PropertyValueFactory<PTable, BigDecimal>("bigBlind"));
		tableView.getItems().setAll(parseUserList());
	}

	@FXML
	protected void handleConnectToTable() {
		PTable table = tableView.getSelectionModel().getSelectedItem();
		if (table == null) {
			alert.showAndWait();
		} else {
			//TODO JOIN
			System.out.println(table.getType());
		}
	}
	
	@FXML
	protected void handleCreateTable() {
		frameController.setCreateTableFrame();
	}
}
