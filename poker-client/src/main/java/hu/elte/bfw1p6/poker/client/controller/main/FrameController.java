package hu.elte.bfw1p6.poker.client.controller.main;

import java.io.IOException;

import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FrameController {
	
	private String FXML_PREFIX = "/fxml/";
	private String LOGIN_FXML = "Login.fxml";
	private String REGISTRATION_FXML = "Registration.fxml";
	private String CREATE_TABLE_FXML = "CreateTable.fxml";
	private String TABLE_LISTER_FXML = "TableLister.fxml";
	
	private Scene scene;
	
	public FrameController(Scene scene) {
//		setCreateTableFrame();
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
	
	public void setCreateTableFrame() {
		setFXML(FXML_PREFIX + CREATE_TABLE_FXML);
	}
	
	public void setFXML(String resource) {
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
