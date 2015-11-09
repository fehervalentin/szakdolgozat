package hu.elte.bfw1p6.poker.client.controller.main;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A kliens ezen controller segítségével tud az FXML-ek között váltani.
 * @author feher
 *
 */
public class FrameController extends UnicastRemoteObject {
	
	private static final long serialVersionUID = 4102088160605201971L;
	
	private final String FXML_PREFIX = "/fxml/";
	private final String LOGIN_FXML = "Login.fxml";
	private final String USERS_FXML = "UserLister.fxml";
	private final String CREATE_TABLE_FXML = "CreateTable.fxml";
	private final String TABLE_LISTER_FXML = "TableLister.fxml";
	private final String REGISTRATION_FXML = "Registration.fxml";
	private final String PROFILE_MANAGER_FXML = "ProfileManager.fxml";
	private final String HOLDEM_MAIN_GAME_FXML = "HoldemMainGame.fxml";
	private final String CLASSIC_MAIN_GAME_FXML = "ClassicMainGame.fxml";
	
	
	
	private final int MENU_GAME_WIDHT = 900;
	private final int MENU_GAME_HEIGHT = 500;
	
	private final int MAIN_GAME_WIDHT = 1366;
	private final int MAIN_GAME_HEIGHT = 768;
	
	private Scene scene;
	
	private Stage stage;
	
	public FrameController(Stage stage) throws RemoteException {
		this.stage = stage;
//		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setStageSize(MENU_GAME_WIDHT, MENU_GAME_HEIGHT);
		setLoginFXML();
//		setClassicMainGameFXML();
		
	}

	/**
	 * A regisztráció FXML-t jeleníti meg.
	 */
	public void setRegistrationFXML() {
		setFXML(FXML_PREFIX + REGISTRATION_FXML);
	}
	
	/**
	 * A bejelentkező FXML-t jeleníti meg.
	 */
	public void setLoginFXML() {
		setFXML(FXML_PREFIX + LOGIN_FXML);
	}
	
	/**
	 * A tábla listázó FXML-t jeleníti meg.
	 */
	public void setTableListerFXML() {
		setFXML(FXML_PREFIX + TABLE_LISTER_FXML);
	}
	
	/**
	 * A tábla létrehozó FXML-t jeleníti meg.
	 */
	public void setCreateTableFXML() {
		setFXML(FXML_PREFIX + CREATE_TABLE_FXML);
	}
	
	private void setHoldemMainGameFXML() {
		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setFXML(FXML_PREFIX + HOLDEM_MAIN_GAME_FXML);
	}
	
	private void setClassicMainGameFXML() {
		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setFXML(FXML_PREFIX + CLASSIC_MAIN_GAME_FXML);
	}
	
	/**
	 * A profil menedzselő FXML-t jeleníti meg.
	 */
	public void setProfileManagerFXML() {
		setFXML(FXML_PREFIX + PROFILE_MANAGER_FXML);
	}
	
	/**
	 * A póker játék fő-FXML-t jeleníti meg.
	 */
	public void setMainGameFXML(PokerType pokerType) {
		switch (pokerType) {
		case HOLDEM:
			setHoldemMainGameFXML();
			break;
		case CLASSIC:
			setClassicMainGameFXML();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * A felhasználó listázó FXML-t jeleníti meg.
	 */
	public void setUsersFXML() {
		setFXML(FXML_PREFIX + USERS_FXML);
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
	
	private void setStageSize(int width, int height) {
		stage.setWidth(width);
		stage.setHeight(height);
	}
}