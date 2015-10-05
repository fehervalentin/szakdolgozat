package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.Type;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class TableCreatorController implements Initializable, PokerController {

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
	private Label limitLabel;

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
	private TextField limitTextField;

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


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new Model();
	}

	@FXML protected void handleCreateTableButton(ActionEvent event) {
		System.out.println("lol");
//		PTable table = new PTable(tableNameLabel.getText(),
//				Integer.valueOf(maxTimeField.getText()),
//				Integer.valueOf(maxPlayerTextField.getText()),
//				BigDecimal.valueOf(Double.valueOf(limitTextField.getText())),
//				BigDecimal.valueOf(Double.valueOf(smallBlindTextField.getText())),
//				BigDecimal.valueOf(Double.valueOf(bigBlindtTextField.getText())));
		PTable table = new PTable("asd",
				5,
				2,
				new BigDecimal(2),
				new BigDecimal(2),
				new BigDecimal(2));
		table.setType(Type.HOLDEM);
		/*try {
			model.createTable(table);
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/
		List<PTable> tables = new ArrayList<>();//model.getTables();
		tables.add(table);
		System.out.println(tables);
		System.out.println("vege");
	}

	@FXML
	protected void createTableHandler(ActionEvent event) {
		frameController.setLoginFXML();
	}

	@FXML
	protected void backHandler(ActionEvent event) {

	}

	public void setDelegateController(FrameController fc) {
		this.frameController = fc;
	}

}
