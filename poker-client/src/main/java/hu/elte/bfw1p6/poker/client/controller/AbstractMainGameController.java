package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerObserverController;
import hu.elte.bfw1p6.poker.client.model.AbstractMainGameModel;
import hu.elte.bfw1p6.poker.client.view.AbstractMainView;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public abstract class AbstractMainGameController implements Initializable, PokerClientController, PokerObserverController{
	
	@FXML protected AnchorPane mainGamePane;

	@FXML protected Label pokerLabel;

	@FXML protected Button callButton;
	@FXML protected Button checkButton;
	@FXML protected Button raiseButton;
	@FXML protected Button foldButton;
	@FXML protected Button quitButton;
	

	protected AbstractMainView mainView;

	protected AbstractMainGameModel model;

	protected FrameController frameController;

	protected CommunicatorController commController;

	protected Scene scene;

	protected Alert errorAlert;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		this.scene = this.frameController.getScene();
	}
	
	public abstract void initialize(URL location, ResourceBundle resources);
	
	public abstract void updateMe(Object updateMsg);
	

	protected void receivedBlindHouseCommand(HouseCommand houseCommand) {
		try {
			mainView.receivedBlindHouseCommand(houseCommand);
			model.receivedBlindHouseCommand(houseCommand);
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	protected void receivedDealHouseCommand(HouseCommand houseCommand) {
		model.receivedDealHouseCommand(houseCommand);
		modifyButtonVisibilities(houseCommand);
		mainView.receivedDealHouseCommand(houseCommand);
	}

	protected void receivedWinnerHouseCommand(HouseCommand houseCommand) {
		mainView.winner(houseCommand);
	}
	

	protected void modifyButtonVisibilities(PokerCommand pokerCommand) {
		if (pokerCommand instanceof HoldemHouseCommand) {
			pokerCommand = (HoldemHouseCommand)pokerCommand;
		} else if (pokerCommand instanceof HoldemPlayerCommand) {
			pokerCommand = (HoldemPlayerCommand)pokerCommand;
		}
		boolean disable = model.getYouAreNth() == pokerCommand.getWhosOn() ? false : true;
		System.out.println("Button disability: " + model.getYouAreNth() + " " + pokerCommand.getWhosOn());
		modifyButtonsDisability(disable);
	}

	protected void modifyButtonsDisability(boolean disable) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				callButton.setDisable(disable);
				checkButton.setDisable(disable);
				foldButton.setDisable(disable);
				raiseButton.setDisable(disable);
			}
		});
	}
	

	protected void showErrorAlert(String msg) {
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}
	
	/**
	 * A <b>CALL</b> gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleCall(ActionEvent event) {
		try {
			model.call();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	@FXML protected void handleCheck(ActionEvent event) {
		try {
			model.check();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	@FXML protected void handleRaise(ActionEvent event) {
		try {
			model.raise(new BigDecimal(6));
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	@FXML protected void handleFold(ActionEvent event) {
		try {
			model.fold();
			mainView.fold();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	@FXML protected void handleQuit(ActionEvent event) {
		/*try {
			model.quit();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}*/
		frameController.setHoldemMainGameFXML();
		//		frameController.setTableListerFXML();
	}
	


	
	

	protected void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		model.receivedBlindPlayerCommand(playerCommand);
		mainView.receivedBlindPlayerCommand(playerCommand);
	}

	protected void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		model.receivedCallPlayerCommand(playerCommand);
		mainView.receivedCallPlayerCommand(playerCommand);
	}

	protected void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		model.receivedCheckPlayerCommand(playerCommand);
		mainView.receivedCheckPlayerCommand(playerCommand);
	}

	protected void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		model.receivedFoldPlayerCommand(playerCommand);
		mainView.receivedFoldPlayerCommand(playerCommand);
	}

	protected void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		model.receivedRaisePlayerCommand(playerCommand);
		mainView.receivedRaisePlayerCommand(playerCommand);
		checkButton.setDisable(true);
	}

	protected void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		model.receivedQuitPlayerCommand(playerCommand);
		mainView.receivedQuitPlayerCommand(playerCommand);
	}
}
