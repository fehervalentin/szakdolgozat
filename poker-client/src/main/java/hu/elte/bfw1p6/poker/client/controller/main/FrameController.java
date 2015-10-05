package hu.elte.bfw1p6.poker.client.controller.main;

import java.io.IOException;

import hu.elte.bfw1p6.poker.client.controller.PokerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FrameController {
	
	private String FXML_PREFIX = "/fxml/";
	private String LOGIN_FXML = "Login.fxml";
	private String REGISTRATION_FXML = "Registration.fxml";
	private String CREATE_TABLE_FXML = "TableCreator.fxml";
	
	private Scene scene;
	
	public FrameController(Scene scene) {
		setCreateTableFrame();
	}

	public void setRegistrationFXML() {
		setFXML(FXML_PREFIX + REGISTRATION_FXML);
	}
	
	public void setLoginFXML() {
		setFXML(FXML_PREFIX + LOGIN_FXML);
	}
	
	public void setCreateTableFrame() {
		setFXML(FXML_PREFIX + CREATE_TABLE_FXML);
	}
	
	public void setFXML(String resource) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
		try {
			this.scene = new Scene(loader.load());
//			scene.setRoot(loader3.load());
			PokerController controller = loader.<PokerController>getController();
			controller.setDelegateController(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Scene getScene() {
		return scene;
	}
}
