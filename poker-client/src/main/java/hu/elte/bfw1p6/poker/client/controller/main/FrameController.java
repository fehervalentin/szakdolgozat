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
	private final String CONNECTOR_FXML = "Connector.fxml";
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
//		setConnectorFXML();
		setLoginFXML();
//		setClassicMainGameFXML();
		
	}
	
	/**
	 * A csatlakozásos FXML-t jeleníti meg.
	 */
	public void setConnectorFXML() {
		setFXML(CONNECTOR_FXML);
	}

	/**
	 * A regisztráció FXML-t jeleníti meg.
	 */
	public void setRegistrationFXML() {
		setFXML(REGISTRATION_FXML);
	}
	
	/**
	 * A bejelentkező FXML-t jeleníti meg.
	 */
	public void setLoginFXML() {
		setFXML(LOGIN_FXML);
	}
	
	/**
	 * A tábla listázó FXML-t jeleníti meg.
	 */
	public void setTableListerFXML() {
		setStageSize(MENU_GAME_WIDHT, MENU_GAME_HEIGHT);
		setFXML(TABLE_LISTER_FXML);
	}
	
	/**
	 * A tábla létrehozó FXML-t jeleníti meg.
	 */
	public void setCreateTableFXML() {
		setFXML(CREATE_TABLE_FXML);
	}
	
	private void setHoldemMainGameFXML() {
		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setFXML(HOLDEM_MAIN_GAME_FXML);
	}
	
	private void setClassicMainGameFXML() {
		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setFXML(CLASSIC_MAIN_GAME_FXML);
	}
	
	/**
	 * A profil menedzselő FXML-t jeleníti meg.
	 */
	public void setProfileManagerFXML() {
		setFXML(PROFILE_MANAGER_FXML);
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
		setFXML(USERS_FXML);
	}
	
	private void setFXML(String resource) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PREFIX + resource));
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