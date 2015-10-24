package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerObserverController;
import hu.elte.bfw1p6.poker.client.model.MainGameModel;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainGameController implements Initializable, PokerClientController, PokerObserverController {

	@FXML private Label pokerLabel;

	@FXML private Button callButton;
	@FXML private Button checkButton;
	@FXML private Button raiseButton;
	@FXML private Button foldButton;
	@FXML private Button quitButton;

	private MainGameModel model;

	private FrameController frameController;

	private CommunicatorController commController;

	private Alert errorAlert;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new MainGameModel(commController);
		errorAlert = new Alert(AlertType.ERROR);
		pokerLabel.setText(model.getUserName());

		try {
			commController = new CommunicatorController(this);
			model.connectToTable(commController);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PokerTooMuchPlayerException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
			frameController.setTableListerFXML();
		} catch (PokerUnauthenticatedException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
			frameController.setLoginFXML();
		}

		callButton.setDisable(true);
		checkButton.setDisable(true);
		raiseButton.setDisable(true);
		foldButton.setDisable(true);
	}

	@Override
	public void updateMe(Object updateMsg) {
		// ha a ház küld utasítást
		if (updateMsg instanceof HouseHoldemCommand) {
			HouseHoldemCommand houseHoldemCommand = (HouseHoldemCommand)updateMsg;
			printHouseCommand(houseHoldemCommand);
			switch (houseHoldemCommand.getHouseCommandType()) {
			case BLIND: {
				try {
					model.blind(houseHoldemCommand);
				} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
					errorAlert.setContentText(e.getMessage());
					errorAlert.showAndWait();
				}
				break;
			}
			case PLAYER: {
				//				player(houseHoldemCommand);
				modifyButtonVisibilities(houseHoldemCommand);
				break;
			}
			case FLOP: {
				modifyButtonVisibilities(houseHoldemCommand);
				break;
			}
			case TURN: {
				modifyButtonVisibilities(houseHoldemCommand);
				break;
			}
			case RIVER: {
				modifyButtonVisibilities(houseHoldemCommand);
				break;
			}
			default: {
				break;
			}
			}
		} else if (updateMsg instanceof PlayerHoldemCommand) {
			PlayerHoldemCommand playerHoldemCommand = (PlayerHoldemCommand)updateMsg;
			System.out.println(playerHoldemCommand.getSender() + " " + playerHoldemCommand.getPlayerCommandType());
			System.out.println("You are nth: " + model.getYouAreNth() + " Whoson: " + playerHoldemCommand.getWhosOn());
			modifyButtonVisibilities(playerHoldemCommand);
			// TODO: megjelenítés miatt maradt meg a switch-case...!
			switch (playerHoldemCommand.getPlayerCommandType()) {
			case BLIND: {
				model.receivedBlindCommand(playerHoldemCommand);
				break;
			}
			case CALL: {
				model.receivedCallCommand(playerHoldemCommand);
				break;
			}
			case CHECK: {
				model.receivedCheckCommand(playerHoldemCommand);
				break;
			}
			case FOLD: {
				model.receivedFoldCommand(playerHoldemCommand);
				break;
			}
			case RAISE: {
				model.receivedRaiseCommand(playerHoldemCommand);
				checkButton.setDisable(true);
				break;
			}
			case QUIT: {
				model.receivedQuitCommand(playerHoldemCommand);
				break;
			}
			default: {
				break;
			}
			}
		} else {
			throw new IllegalArgumentException();
		}
		System.out.println("Adósságom: " + model.getMyDebt());
		// ha van adósságom
		if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
			checkButton.setDisable(true);
		} else {
			callButton.setDisable(true);
		}
	}

	private void modifyButtonVisibilities(PokerCommand pokerCommand) {
		if (pokerCommand instanceof HouseHoldemCommand) {
			pokerCommand = (HouseHoldemCommand)pokerCommand;
		} else if (pokerCommand instanceof PlayerHoldemCommand) {
			pokerCommand = (PlayerHoldemCommand)pokerCommand;
		}
		if (model.getYouAreNth() == pokerCommand.getWhosOn()) {
			enableButtons();
		} else {
			disableButtons();
		}
	}

	private void disableButtons() {
		callButton.setDisable(true);
		checkButton.setDisable(true);
		foldButton.setDisable(true);
		raiseButton.setDisable(true);
	}

	private void enableButtons() {
		callButton.setDisable(false);
		checkButton.setDisable(false);
		foldButton.setDisable(false);
		raiseButton.setDisable(false);
	}

	private void player(HouseHoldemCommand houseHoldemCommand) {
		if (model.getYouAreNth() == houseHoldemCommand.getWhosOn()) {
			if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
				enableButtons();
				checkButton.setDisable(true);
			}
		} else {
			disableButtons();
		}
	}
	
	private void printHouseCommand(HouseHoldemCommand command) {
		System.out.println("----------------");
		System.out.println(command);
	}

	/**
	 * A <b>CALL</b> gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleCall(ActionEvent event) {
		try {
			model.call();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@FXML protected void handleCheck(ActionEvent event) {
		try {
			model.check();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@FXML protected void handleRaise(ActionEvent event) {
		try {
			model.raise(new BigDecimal(6));
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@FXML protected void handleFold(ActionEvent event) {
		try {
			model.fold();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@FXML protected void handleQuit(ActionEvent event) {
		try {
			model.quit();
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
		frameController.setTableListerFXML();
	}

	@Override
	public PokerPlayer getPlayer() {
		return model.getPlayer();
	}
}