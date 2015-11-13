package hu.elte.bfw1p6.poker.client.controller.game;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.AbstractMainGameModel;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.client.view.AbstractMainView;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Abstract játék controller. A classic és a holdem controllerek közös részei itt találhatóak.
 * @author feher
 *
 */
public abstract class AbstractMainGameController implements PokerClientController, PokerRemoteObserver {

	protected final String ERR_CONN = "Megszakadt a kommunikáció a szerverrel!";

	@FXML protected AnchorPane mainGamePane;

	@FXML protected Label pokerLabel;

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

	/**
	 * Időzített feladatokat végrehajtó objektum.
	 */
	//	protected Timer automateExecution;

	/**
	 * Az időzítendő feladat.
	 */
	//	protected TimerTask timerTask;

	//TODO: játékszerver specifikus
	@Deprecated
	protected long delay = 5000;

	//TODO: játékszerver specifikus
	@Deprecated
	private double raiseAmount = 6;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		this.scene = this.frameController.getScene();
	}

	/**
	 * Új időzített feladatot hoz létre az automatikus cselekedéshez. (Check, change, fold gombok fire metódusának hívására.)
	 * @return az új időzített feladat
	 */
	protected abstract TimerTask createTimerTask();

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
		} catch (PokerUnauthenticatedException | RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A szerver DEAL típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	protected void receivedDealHouseCommand(HouseCommand houseCommand) {
		modifyButtonsDisability(houseCommand);
		model.receivedDealHouseCommand(houseCommand);
		mainView.receivedDealHouseCommand(houseCommand);
	}

	/**
	 * A szerver WINNER típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	protected void receivedWinnerHouseCommand(HouseCommand houseCommand) {
		Card[] cards = houseCommand.getCards();
		System.out.println("Winner cards: " + Arrays.toString(cards));
		modifyButtonsDisability(null);
		System.out.println("WhosOn: " + houseCommand.getWhosOn() + " YouAreNth: " + model.getYouAreNth());
		if (houseCommand.getWhosOn() == model.getYouAreNth()) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					checkButton.setDisable(false);
				}
			});
		}
		mainView.receivedWinnerHouseCommand(houseCommand);
	}


	/**
	 * Módosítja a CALL, CHECK, FOLD, RAISE gombok disable tulajdonságát. Ha én nem jövök, vagy ha null értékkel hívom meg a függvényt, akkor letiltja a gombokat.
	 * @param pokerCommand az utasítás
	 */
	protected void modifyButtonsDisability(PokerCommand pokerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				boolean disable = (pokerCommand == null || model.getYouAreNth() != pokerCommand.getWhosOn()) ? true : false;
				callButton.setDisable(disable);
				checkButton.setDisable(disable);
				foldButton.setDisable(disable);
				raiseButton.setDisable(disable);
				quitButton.setDisable(disable);
			}
		});
	}

	/**
	 * Módosítja a FOLD button disable tulajdonságát.
	 * @param disabled
	 */
	protected void modifyFoldButtonDisability(boolean disabled) {
		foldButton.setDisable(disabled);
	}

	/**
	 * Módosítja a CHECK button disable tulajdonságát.
	 * @param disabled
	 */
	protected void modifyCheckButtonDisability(boolean disabled) {
		checkButton.setDisable(disabled);
	}

	/**
	 * Felugró ablakot jelenít meg.
	 * @param msg az üzenet
	 */
	protected void showErrorAlert(String msg) {
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}

	/**
	 * A CALL gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleCall(ActionEvent event) {
		try {
			//			timerTask.cancel();
			model.sendCallCommand();
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException | PokerUnauthenticatedException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A CHECK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleCheck(ActionEvent event) {
		try {
			//			timerTask.cancel();
			model.sendCheckCommand();
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (PokerUnauthenticatedException | RemoteException e ) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A RAISE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleRaise(ActionEvent event) {
		try {
			//			timerTask.cancel();
			model.sendRaiseCommand(new BigDecimal(raiseAmount));
			mainView.setBalance(model.getBalance());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (PokerUnauthenticatedException | RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A FOLD gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleFold(ActionEvent event) {
		try {
			//			timerTask.cancel();
			model.sendFoldCommand();
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (PokerUnauthenticatedException | RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A QUIT gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleQuit(ActionEvent event) {
		//		if (timerTask != null) {
		//			timerTask.cancel();
		//		}
		try {
			model.sendQuitCommand();
			frameController.setTableListerFXML();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
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
			modifyButtonsDisability(null);
		}
		System.out.println("AZENYÉMBAZDMEGWhosOn: " + playerCommand.getWhosOn() + " YouAreNth: " + model.getYouAreNth() + " ISWINNER: " + playerCommand.isWinnerCommand());
		if (playerCommand.getWhosOn() == model.getYouAreNth() && playerCommand.isWinnerCommand()) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					checkButton.setDisable(false);
				}
			});
		}
		mainView.receivedCheckPlayerCommand(playerCommand);
	}

	/**
	 * FOLD típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		model.receivedFoldPlayerCommand(playerCommand);
		mainView.receivedFoldPlayerCommand(playerCommand);
	}

	/**
	 * RAISE típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		System.out.println("A RAISE mértéke: " + playerCommand.getRaiseAmount());
		model.receivedRaisePlayerCommand(playerCommand);
		mainView.receivedRaisePlayerCommand(playerCommand);
		checkButton.setDisable(true);
	}

	/**
	 * QUIT típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	protected void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		//TODO: playerCommandban jöjjön le a kliensek száma 
		if (playerCommand.getClientsCount() < 2) {
			modifyButtonsDisability(null);
		}
		model.receivedQuitPlayerCommand(playerCommand);
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
	 * Ellenőrzi, hogy van-e addósságom, és ennek megfelelően állítja be a gombok disable tulajdonsgát.
	 */
	protected void debtChecker() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
					checkButton.setDisable(true);
				} else {
					callButton.setDisable(true);
				}
			}
		});
	}
}
