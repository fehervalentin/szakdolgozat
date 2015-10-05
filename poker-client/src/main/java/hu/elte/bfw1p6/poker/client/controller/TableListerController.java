package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.Type;
import hu.elte.bfw1p6.poker.model.entity.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableListerController implements PokerController {
	
	private FrameController framceController;
	
	@FXML private TableView<PTable> tableView;
	@FXML private TableColumn<PTable, String> tableName;
    @FXML private TableColumn<PTable, String> maxTime;
    @FXML private TableColumn<PTable, String> maxPlayers;

	
	public TableListerController() {
//		addTable();
		akarmi();
	}
	
	protected void addTable() {
		PTable table = new PTable("asd",
				5,
				2,
				new BigDecimal(2),
				new BigDecimal(2),
				new BigDecimal(2));
		table.setType(Type.HOLDEM);
		ObservableList<PTable> data = tableView.getItems();
        data.add(table);
	}
	
	private void akarmi() {
		tableName.setCellValueFactory(new PropertyValueFactory<PTable, String>("id2"));
//		maxTime.setCellValueFactory(new PropertyValueFactory<PTable, String>("name2"));
//		maxPlayers.setCellValueFactory(new PropertyValueFactory<PTable, String>("active"));

//        tableView.getItems().setAll(parseUserList());
	}
	
	private List<PTable> parseUserList(){
		PTable table = new PTable("asd",
				5,
				2,
				new BigDecimal(2),
				new BigDecimal(2),
				new BigDecimal(2));
		table.setType(Type.HOLDEM);
        // parse and construct User datamodel list by looping your ResultSet rs
        // and return the list  
		List<PTable> anyad = new ArrayList<>();
		anyad.add(table);
		return anyad;
    }

	@Override
	public void setDelegateController(FrameController frameController) {
		this.framceController = framceController;
	}
}
