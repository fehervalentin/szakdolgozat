package hu.elte.bfw1p6.poker.client.controller.main;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.application.Platform;
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
	private final String FXML_EXTENSION = ".fxml";
	
	private final String LOGIN_FXML = "Login";
	private final String USERS_FXML = "UserLister";
	private final String CONNECTOR_FXML = "Connector";
	private final String CREATE_TABLE_FXML = "CreateTable";
	private final String TABLE_LISTER_FXML = "TableLister";
	private final String REGISTRATION_FXML = "Registration";
	private final String PROFILE_MANAGER_FXML = "ProfileManager";
	private final String HOLDEM_MAIN_GAME_FXML = "HoldemMainGame";
	private final String CLASSIC_MAIN_GAME_FXML = "ClassicMainGame";
	
	private final int MENU_GAME_WIDHT = 900;
	private final int MENU_GAME_HEIGHT = 500;
	
	private final int MAIN_GAME_WIDHT = 1366;
	private final int MAIN_GAME_HEIGHT = 768;
	
	private Scene scene;
	
	private Stage stage;
	
	private FrameController fc;
	
	public FrameController(Stage stage) throws RemoteException {
		this.stage = stage;
		this.fc = this;
//		setStageSize(MAIN_GAME_WIDHT, MAIN_GAME_HEIGHT);
		setStageSize(MENU_GAME_WIDHT, MENU_GAME_HEIGHT);
		setConnectorFXML();
//		setLoginFXML();
//		setClassicMainGameFXML();
	}
	
	/**
	 * Csatlakozás a szerverhez FXML-t jeleníti meg.
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
	 * @param pokerType a játékstílus
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
	
	/**
	 * FXML állít be a színtérre.
	 * @param resource a beállítandó fxml neve
	 */
	private synchronized void setFXML(String resource) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PREFIX + resource + FXML_EXTENSION));
				try {
					if (scene == null) {
						scene = new Scene(loader.load());
					} else {
						scene.setRoot(loader.load());
					}
					PokerClientController controller = loader.<PokerClientController>getController();
					controller.setDelegateController(fc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Scene getScene() {
		return scene;
	}
	
	private void setStageSize(int width, int height) {
		stage.setWidth(width);
		stage.setHeight(height);
	}
}