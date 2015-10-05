package hu.elte.bfw1p6.poker.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FrameController {
	
	private String FXML_PREFIX = "/fxml/";
	private String LOGIN_FXML = "Login.fxml";
	private String REGISTRATION_FXML = "Registration.fxml";
	
	private FXMLLoader loader = null;
	private FXMLLoader loader2 = null;
	private Scene scene;
	
	public FrameController(Scene scene) {
		loader = new FXMLLoader( getClass().getResource(FXML_PREFIX + LOGIN_FXML));
		this.scene = scene;
		try {
			this.scene = new Scene(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		LoginController controller = loader.<LoginController>getController();
		controller.setDelegateController(this);
	}

	public void goToReg() {
		loader2 = new FXMLLoader(getClass().getResource(FXML_PREFIX + REGISTRATION_FXML));
		/*if (null == loader2) {
			loader2 = new FXMLLoader( getClass().getResource(FXML_PREFIX + REGISTRATION_FXML));
		}*/
		try {
			scene.setRoot(loader2.load());
			RegistrationController controller = loader2.<RegistrationController>getController();
			controller.setDelegateController(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void goToLogin() {
		loader = new FXMLLoader(getClass().getResource(FXML_PREFIX + LOGIN_FXML));
		/*if (null == loader) {
			loader = new FXMLLoader( getClass().getResource(FXML_PREFIX + LOGIN_FXML));
		}*/
		try {
			scene.setRoot(loader.load());
			LoginController controller = loader.<LoginController>getController();
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
