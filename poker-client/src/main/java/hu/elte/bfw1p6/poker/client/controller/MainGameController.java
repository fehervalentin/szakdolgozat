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
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class MainGameController implements Initializable, PokerClientController, PokerObserverController {


	@FXML private Button checkButton;
	@FXML private Button raiseButton;
	@FXML private Button foldButton;
	@FXML private Button quitButton;

	private MainGameModel model;

	private FrameController frameController;

	private PokerTable pokerTable;

	private CommunicatorController commController;

	/**
	 * Jelenleg ki következik
	 */
	private int whosOn = 0;

	/**
	 * Hanyadik vagyok a körben
	 */
	private int youAreNth;

	/**
	 * Hány játékos van velem együtt
	 */
	private int players;
	
	/**
	 * Az aktuális osztó sorszáma
	 */
	private int dealer;

	private Alert errorAlert;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorAlert = new Alert(AlertType.ERROR);

		pokerTable = ConnectTableHelper.getInstance().getPokerTable();
		try {
			commController = new CommunicatorController(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model = new MainGameModel();
		try {
			model.connectToTable(pokerTable, commController);
		} catch (RemoteException | PokerTooMuchPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PokerUnauthenticatedException e) {
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	@Override
	public void updateMe(Object updateMsg) {
		// ha a ház küld utasítást
		if (updateMsg instanceof HouseHoldemCommand) {
			HouseHoldemCommand houseHoldemCommand = (HouseHoldemCommand)updateMsg;
			printHouseCommand(houseHoldemCommand);
			switch (houseHoldemCommand.getHouseCommandType()) {
			case BLIND: {
				youAreNth = houseHoldemCommand.getNthPlayer();
				players = houseHoldemCommand.getPlayers();
				dealer = houseHoldemCommand.getDealer();
				if (youAreNth == ((dealer + 1) % players)) {
					System.out.println("kivak");
					smallBlind();
				} else if (youAreNth == ((dealer + 2) % players)) {
					bigBlind();
					System.out.println("nagyvak");
				}
				break;
			}
			case PLAYER: {
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			case FLOP: {
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			case TURN: {
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			case RIVER: {
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			default: {
				break;
			}
			}
		} else if (updateMsg instanceof PlayerHoldemCommand) {
			PlayerHoldemCommand playerHoldemCommand = (PlayerHoldemCommand)updateMsg;
			HoldemPlayerCommandType commandType = playerHoldemCommand.getPlayerCommandType();
			System.out.println(commandType.name());
			if (commandType == HoldemPlayerCommandType.RAISE) {
				System.out.println(playerHoldemCommand.getAmount());
			}
			if (youAreNth == whosOn) {
				enableButtons();
			} else {
				disableButtons();
			}
			if (playerHoldemCommand.getPlayerCommandType() != HoldemPlayerCommandType.QUIT) {
			}
		} else {
			throw new IllegalArgumentException();
		}
		whosOn++;
		whosOn %= players;
	}
	
	private void smallBlind() {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(2));
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.BLIND, amount);
		sendPlayerCommand(playerHoldemCommand);
	}
	
	private void bigBlind() {
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.BLIND, pokerTable.getDefaultPot());
		sendPlayerCommand(playerHoldemCommand);
	}

	private void printHouseCommand(HouseHoldemCommand command) {
		System.out.println("----------------");
		System.out.println(command);
	}
	
	private void sendPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		new Thread() {

			@Override
			public void run() {
				try {
					model.sendCommandToTable(pokerTable, commController, playerHoldemCommand);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (PokerUnauthenticatedException | PokerDataBaseException e) {
					errorAlert.setContentText(e.getMessage());
					errorAlert.showAndWait();
				}
			}}.start();
	}

	private void disableButtons() {
		checkButton.setDisable(true);
		foldButton.setDisable(true);
		raiseButton.setDisable(true);
	}

	private void enableButtons() {
		checkButton.setDisable(false);
		foldButton.setDisable(false);
		raiseButton.setDisable(false);
	}

	@FXML protected void handleCheck(ActionEvent event) {
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.CHECK, null);
		sendPlayerCommand(playerHoldemCommand);
	}

	@FXML protected void handleRaise(ActionEvent event) {
	}

	@FXML protected void handleFold(ActionEvent event) {
	}

	@FXML protected void handleQuit(ActionEvent event) {
	}
}
