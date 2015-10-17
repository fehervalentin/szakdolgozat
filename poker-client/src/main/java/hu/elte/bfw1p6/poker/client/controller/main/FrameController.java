package hu.elte.bfw1p6.poker.client.controller.main;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FrameController extends UnicastRemoteObject {
	
	private static final long serialVersionUID = 1L;
	
	
	private final String FXML_PREFIX = "/fxml/";
	private final String LOGIN_FXML = "Login.fxml";
	private final String REGISTRATION_FXML = "Registration.fxml";
	private final String CREATE_TABLE_FXML = "CreateTable.fxml";
	private final String TABLE_LISTER_FXML = "TableLister.fxml";
	private final String MAIN_GAME_FXML = "MainGame.fxml";
	private final String PROFILE_MANAGER_FXML = "ProfileManager.fxml";
	
	private Scene scene;
	
	public FrameController(Scene scene) throws RemoteException {
		setLoginFXML();
	}

	public void setRegistrationFXML() {
		setFXML(FXML_PREFIX + REGISTRATION_FXML);
	}
	
	public void setLoginFXML() {
		setFXML(FXML_PREFIX + LOGIN_FXML);
	}
	
	public void setTableListerFXML() {
		setFXML(FXML_PREFIX + TABLE_LISTER_FXML);
	}
	
	public void setCreateTableFXML() {
		setFXML(FXML_PREFIX + CREATE_TABLE_FXML);
	}
	
	public void setMainGameFXML() {
		setFXML(FXML_PREFIX + MAIN_GAME_FXML);
	}
	
	public void setProfileManagerFXML() {
		setFXML(FXML_PREFIX + PROFILE_MANAGER_FXML);
	}
	
	private void setFXML(String resource) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
		try {
			if (scene == null) {
				this.scene = new Scene(loader.load());
			} else {
				scene.setRoot(loader.load());
			}
			PokerClientController controller = loader.<PokerClientController>getController();
			controller.setDelegateController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Scene getScene() {
		return scene;
	}
}
