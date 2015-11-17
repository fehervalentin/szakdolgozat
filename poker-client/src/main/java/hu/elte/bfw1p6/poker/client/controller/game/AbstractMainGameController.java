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
	
	@FXML protected TextArea textArea;

	@FXML protected Button callButton;
	@FXML protected Button checkButton;
	@FXML protected Button raiseButton;
	@FXML protected Button foldButton;
	@FXML protected Button quitButton;


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
	 * Módosítja a CALL, CHECK, FOLD, RAISE gombok disable tulajdonságát. Ha én nem jövök, vagy ha null értékkel hívom meg a függvényt, akkor letiltja a gombokat.
	 * @param pokerCommand az utasítás
	 */
	protected void setButtonsDisability(boolean isYourTurn) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				callButton.setDisable(isYourTurn);
				checkButton.setDisable(isYourTurn);
				foldButton.setDisable(isYourTurn);
				raiseButton.setDisable(isYourTurn);
				quitButton.setDisable(isYourTurn);
			}
		});
	}

	protected void setQuitButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				quitButton.setDisable(disabled);
			}
		});
	}
	
	protected void modifyQuitButtonDisability(boolean disabled) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				quitButton.setDisable(disabled);
				
			}
		});
	}

	/**
	 * Módosítja a FOLD button disable tulajdonságát.
	 * @param disabled
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
	 * Módosítja a CHECK button disable tulajdonságát.
	 * @param disabled
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
	 * Felugró ablakot jelenít meg.
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
	 * BLIND típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedCallPlayerCommand(PlayerCommand playerCommand) {
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
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					checkButton.setDisable(false);
					quitButton.setDisable(false);
				}
			});
		}
	}

	/**
	 * FOLD típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		model.receivedFoldPlayerCommand(playerCommand);
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
		if (playerCommand.getClientsCount() < 2) {
			setButtonsDisability(true);
			modifyQuitButtonDisability(false);
		}
//		model.receivedQuitPlayerCommand(playerCommand);
		if (playerCommand.getWhosQuit() == model.getYouAreNth()) {
			frameController.setTableListerFXML();
		}
		mainView.receivedQuitPlayerCommand(playerCommand);
	}

	/**
	 * Hibás szerver-kliens kommunikációt kezelő eljárás.
	 */
	public void remoteExceptionHandler() {
		showErrorAlert(ERR_CONN);
		frameController.setLoginFXML();
	}

	/**
	 * Ellenőrzi, hogy van-e addósságom, és ennek megfelelően állítja be a CHECK és CALL gombok disable tulajdonsgát.
	 */
	protected void debtChecker(int whosOn) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (model.getYouAreNth() == whosOn) {
					if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
						checkButton.setDisable(true);
					} else {
						callButton.setDisable(true);
					}
				}
			}
		});
	}
}