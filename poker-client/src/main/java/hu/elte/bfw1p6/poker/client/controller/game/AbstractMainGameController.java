package hu.elte.bfw1p6.poker.client.controller.game;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.AbstractMainGameModel;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.client.view.AbstractMainView;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Abstract játék controller. A classic és a holdem controllerek közös részei itt találhatóak.
 * @author feher
 *
 */
public abstract class AbstractMainGameController implements PokerClientController, PokerRemoteObserver {

	protected final String ERR_CONN = "Megszakadt a kommunikáció a szerverrel!";
	
	@FXML protected AnchorPane mainGamePane;
	
	@FXML protected ImageView tableImage;

	@FXML protected Button callButton;
	@FXML protected Button checkButton;
	@FXML protected Button raiseButton;
	@FXML protected Button foldButton;
	@FXML protected Button quitButton;
	@FXML protected Button logButton;


	protected TextArea textArea;
	
	/**
	 * A póker játék GUI-ja.
	 */
	protected AbstractMainView mainView;

	/**
	 * A póker játék modelje.
	 */
	protected AbstractMainGameModel model;

	/**
	 * A framek közötti váltásokért felelős objektum.
	 */
	protected FrameController frameController;

	/**
	 * A hálózati kommunikációért felelős objektum.
	 */
	protected CommunicatorController commController;

	/**
	 * A színtérért felelős objektum.
	 */
	protected Scene scene;

	/**
	 * Hibát jelző felugró ablak.
	 */
	protected Alert errorAlert;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		this.scene = this.frameController.getScene();
	}
	
	protected void setTextArea() {
		textArea = new TextArea();
		textArea.setLayoutX(370);
		textArea.setLayoutY(200);
		textArea.setMinSize(500, 300);
		textArea.setDisable(true);
		textArea.setOpacity(0);
		textArea.setFocusTraversable(false);
		mainGamePane.getChildren().add(textArea);
	}

	public abstract void initialize(URL location, ResourceBundle resources);

	/**
	 * A szerver BLIND típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	protected void receivedBlindHouseCommand(HouseCommand houseCommand) {
		try {
			mainView.receivedBlindHouseCommand(houseCommand);
			model.receivedBlindHouseCommand(houseCommand);
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A szerver DEAL típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	protected void receivedDealHouseCommand(HouseCommand houseCommand) {
		for (int i = 0; i < houseCommand.getCards().length; i++) {
			textArea.appendText(houseCommand.getCards()[i].toString() + " ");
		}
		model.receivedDealHouseCommand(houseCommand);
		mainView.receivedDealHouseCommand(houseCommand);
	}

	/**
	 * A szerver WINNER típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	protected void receivedWinnerHouseCommand(HouseCommand houseCommand) {
		textArea.appendText(" Nyertes: " +  mainView.ultimateFormula(houseCommand.getWinner()) + " " +  Arrays.toString(houseCommand.getCards()) + System.lineSeparator());
		setButtonsDisability(true);
		if (houseCommand.getWhosOn() == model.getYouAreNth()) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					checkButton.setDisable(false);
					quitButton.setDisable(false);
				}
			});
		}
		mainView.receivedWinnerHouseCommand(houseCommand);
	}


	/**
	 * Módosítja a CALL, CHECK, FOLD, RAISE, QUIT gombok disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	protected void setButtonsDisability(boolean disabled) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				callButton.setDisable(disabled);
				checkButton.setDisable(disabled);
				foldButton.setDisable(disabled);
				raiseButton.setDisable(disabled);
				quitButton.setDisable(disabled);
			}
		});
	}

	/**
	 * Módosítja a QUIT gomb disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	protected void modifyQuitButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				quitButton.setDisable(disabled);
			}
		});
	}
	
	/**
	 * Módosítja a CALL gomb disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	protected void modifyCallButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				callButton.setDisable(disabled);
			}
		});
	}

	/**
	 * Módosítja a FOLD gomb disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	protected void modifyFoldButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				foldButton.setDisable(disabled);
			}
		});
	}

	/**
	 * Módosítja a CHECK gomb disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	protected void modifyCheckButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				checkButton.setDisable(disabled);
			}
		});
	}

	/**
	 * Hibát jelző felugró ablakot jelenít meg.
	 * @param msg az üzenet
	 */
	protected void showErrorAlert(String msg) {
		if (errorAlert == null) {
			errorAlert = new Alert(AlertType.ERROR);
		}
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}

	/**
	 * A CALL gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleCall(ActionEvent event) {
		try {
			model.sendCallCommand();
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A CHECK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleCheck(ActionEvent event) {
		try {
			model.sendCheckCommand();
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e ) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A RAISE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleRaise(ActionEvent event) {
		try {
			model.sendRaiseCommand();
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A FOLD gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleFold(ActionEvent event) {
		try {
			model.sendFoldCommand();
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A QUIT gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleQuit(ActionEvent event) {
		try {
			model.sendQuitCommand();
			frameController.setTableListerFXML();
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
		catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * BLIND típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		mainView.receivedBlindPlayerCommand(playerCommand);
	}

	/**
	 * CALL típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		if (model.getYouAreNth() ==  playerCommand.getWhosOn()) {
			modifyCheckButtonDisability(false);
			modifyQuitButtonDisability(false);
		}
		mainView.receivedCallPlayerCommand(playerCommand);
	}

	/**
	 * CHECK típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		if (playerCommand.isWinnerCommand()) {
			setButtonsDisability(true);
		}
		mainView.receivedCheckPlayerCommand(playerCommand);
		if (model.getYouAreNth() ==  playerCommand.getWhosOn()) {
			modifyCheckButtonDisability(false);
			modifyQuitButtonDisability(false);
		}
	}
	
	/**
	 * A LOG gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleLog(ActionEvent event) {
		textArea.setOpacity(textArea.getOpacity() == 1 ? 0 : 1);
	}

	/**
	 * FOLD típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		setButtonsDisability(model.getYouAreNth() !=  playerCommand.getWhosOn());
		mainView.receivedFoldPlayerCommand(playerCommand);
	}

	/**
	 * RAISE típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		model.receivedRaisePlayerCommand(playerCommand);
		mainView.receivedRaisePlayerCommand(playerCommand);
	}

	/**
	 * QUIT típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		setButtonsDisability(model.getYouAreNth() !=  playerCommand.getWhosOn());
		if (playerCommand.getClientsCount() == 1) {
			setButtonsDisability(true);
			modifyQuitButtonDisability(false);
		}
		mainView.receivedQuitPlayerCommand(playerCommand);
		//TODO: meg kell szerelni
//		if (playerCommand.getWhosQuit() == model.getYouAreNth()) {
//			frameController.setTableListerFXML();
//		} else {
//			mainView.receivedQuitPlayerCommand(playerCommand);
//		}
	}

	/**
	 * Hibás szerver-kliens kommunikációt kezelő eljárás.
	 */
	public void remoteExceptionHandler() {
		showErrorAlert(ERR_CONN);
		frameController.setLoginFXML();
	}

	/**
	 * Ellenőrzi, hogy van-e adósságom, és ennek megfelelően állítja be a CHECK és CALL gombok disable tulajdonsgát.
	 */
	protected void debtChecker(int whosOn) {
		if (model.getYouAreNth() == whosOn) {
			if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
				modifyCheckButtonDisability(true);
			} else {
				modifyCallButtonDisability(true);
			}
		}
	}
	
	/**
	 * Felhasználó által küldött utasítást logol.
	 * @param playerCommand az utasítás
	 */
	protected void logPlayerCommand(PlayerCommand playerCommand) {
		if (textArea != null) {
			textArea.appendText(System.lineSeparator() + playerCommand.getSender() + ": " + playerCommand.getCommandType());
		}
	}
	
	/**
	 * A ház által küldött utasítást logol.
	 * @param houseCommand
	 */
	protected void logHouseCommand(HouseCommand houseCommand) {
		if (textArea != null) {
			if (houseCommand != null && houseCommand.getCommandType() != null) {
				textArea.appendText(System.lineSeparator() + "Ház: " + houseCommand.getCommandType() + " ");
			} else {
				System.out.println("nem logolt");
			}
		} else {
			System.out.println("textarea null");
		}
	}
}