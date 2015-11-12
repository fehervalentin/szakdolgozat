package hu.elte.bfw1p6.poker.client.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.properties.PokerProperties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Bjelentkezési controller.
 * @author feher
 *
 */
public class ConnectorController extends AbstractPokerClientController {
	
	@FXML private Label pokerLabel;
	
	@FXML private TextField serverNameField;
	@FXML private TextField serverIPField;
	@FXML private TextField serverPortField;
	
	@FXML private Button connectButton;
	
	private PokerProperties properties;
	
	private String SVNAME;
	private String PORT;
	private String SVIP;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.properties = PokerProperties.getInstance();
		this.SVIP = "127.0.0.1";
		this.PORT = properties.getProperty("rmiport");
		this.SVNAME = properties.getProperty("name");
		serverIPField.setText(this.SVIP);
		serverPortField.setText(this.PORT);
		serverNameField.setText(this.SVNAME);
	}

	/**
	 * A CONNECT gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void connectHandler(ActionEvent event) {
		try {
			System.out.println("próbálok!");
			Naming.lookup("//" + SVIP + ":" + PORT + "/" + SVNAME);
			frameController.setLoginFXML();
		} catch (NotBoundException | MalformedURLException | RemoteException e) {
			remoteExceptionHandler();
		}
	}
}