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

	private PokerTable pokerTable;

	private CommunicatorController commController;

	/**
	 * Jelenleg ki következik
	 */
	private int whosOn;

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

	private BigDecimal moneyITookIn;

	private BigDecimal moneyIShouldTookIn;

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new MainGameModel();
		System.out.println(model.getUserName());
		errorAlert = new Alert(AlertType.ERROR);
		pokerLabel.setText(model.getUserName());

		pokerTable = ConnectTableHelper.getInstance().getPokerTable();
		try {
			commController = new CommunicatorController(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				blind(houseHoldemCommand);
				break;
			}
			case PLAYER: {
				player();
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
			switch (commandType) {
			case BLIND: {
				break;
			}
			case CALL: {
				incrementWhosOn();
				System.out.println(playerHoldemCommand.getSender() + " CALL");
				System.out.println("Yrnth: " + youAreNth + " Whoson: " + whosOn);
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			case CHECK: {
				incrementWhosOn();
				if (youAreNth == whosOn) {
					enableButtons();
				} else {
					disableButtons();
				}
				break;
			}
			case FOLD: {
				break;
			}
			case RAISE: {
				break;
			}
			case QUIT: {
				break;
			}
			default: {
				break;
			}
			}
		} else {
			throw new IllegalArgumentException();
		}
		whosOn %= players;
	}
	
	private void incrementWhosOn() {
		++whosOn;
		whosOn %= players;
	}

	private void smallBlind() {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(2));
		moneyITookIn = amount;
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.BLIND, amount);
		sendPlayerCommand(playerHoldemCommand);
	}

	private void bigBlind() {
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.BLIND, pokerTable.getDefaultPot());
		moneyITookIn = pokerTable.getDefaultPot();
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
		callButton.setDisable(true);
		checkButton.setDisable(true);
		foldButton.setDisable(true);
		raiseButton.setDisable(true);
	}

	private void enableButtons() {
		checkButton.setDisable(false);
		foldButton.setDisable(false);
		raiseButton.setDisable(false);
	}

	/**
	 * Ha BLIND utasítás jött a szervertől
	 * @param houseHoldemCommand a szerver utasítás
	 */
	private void blind(HouseHoldemCommand houseHoldemCommand) {
		youAreNth = houseHoldemCommand.getNthPlayer();
		System.out.println("yournth: " + youAreNth);
		players = houseHoldemCommand.getPlayers();
		// első körben az a dealer, aki elsőként csatlakozott, roundonként +1
		dealer = houseHoldemCommand.getDealer();
		System.out.println("Dealer: " + dealer);
		moneyIShouldTookIn = pokerTable.getDefaultPot();
		// dealer mellett eggyel balra ülök, akkor én vagyok a kisvak
		if (youAreNth == ((dealer + 1) % players)) {
			System.out.println("kisvak");
			smallBlind();
			// dealer mellett kettővel balra ülök, akkor én vagyok a nagyvak
		} else if (youAreNth == ((dealer + 2) % players)) {
			System.out.println("nagyvak");
			bigBlind();
		}
		// nagyvaktól eggyel balra ülő kezd
		whosOn = ((dealer + 3) % players);
		System.out.println("Whoson: " + whosOn);
	}

	private void player() {
		System.out.println(youAreNth + " " + whosOn);
		if (youAreNth == whosOn) {
			if (moneyITookIn.compareTo(moneyIShouldTookIn) < 0) {
				enableButtons();
				System.out.println("Beraktam: " + moneyITookIn);
				System.out.println("be kell raknom még ennyit: " + moneyIShouldTookIn.subtract(moneyITookIn));
				checkButton.setDisable(true);
			}
		} else {
			disableButtons();
		}
	}
	
	@FXML protected void handleCall(ActionEvent event) {
		moneyIShouldTookIn.subtract(moneyITookIn);
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.CALL, moneyIShouldTookIn.subtract(moneyITookIn));
		sendPlayerCommand(playerHoldemCommand);
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
