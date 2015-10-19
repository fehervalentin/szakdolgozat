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
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

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

	private PokerTable pokerTable;

	private CommunicatorController commController;

	private Alert errorAlert;
	
	/**
	 * Jelenleg ki következik.
	 */
	private int whosOn;

	/**
	 * Hanyadik vagyok a körben.
	 */
	private int youAreNth;

	/**
	 * Hány játékos van velem együtt.
	 */
	private int players;

	/**
	 * Az aktuális osztó sorszáma.
	 */
	private int dealer;

	/**
	 * A tartozásom az asztal felé, amit <b>CALL</b> vagy <b>RAISE</b> esetén meg kell adnom.
	 */
	private BigDecimal myDebt;
	
	/**
	 * Az összeg, amit már betettem a potba.
	 */
	private BigDecimal alreadInPot;

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
		myDebt = pokerTable.getDefaultPot();
		alreadInPot = BigDecimal.ZERO;
		try {
			commController = new CommunicatorController(this);
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
				myDebt = BigDecimal.ZERO;
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
			switch (playerHoldemCommand.getPlayerCommandType()) {
			case BLIND: {
				break;
			}
			case CALL: {
				incrementWhosOn();
				System.out.println(playerHoldemCommand.getSender() + " CALL");
//				System.out.println("Yrnth: " + youAreNth + " Whoson: " + whosOn);
				if (youAreNth == whosOn) {
					enableButtons();
//					System.out.println("Adósságom: " + myDebt);
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
				incrementWhosOn();
				break;
			}
			case RAISE: {
				incrementWhosOn();
				// és mi van ha én magam emeltem...
				if (!playerHoldemCommand.getSender().equals(model.getUserName())) {
					myDebt = myDebt.add(playerHoldemCommand.getRaiseAmount());
				} else { // ha én magam emeltem, akkor a szerver elszámolta a teljes adósságom
					myDebt = BigDecimal.ZERO;
				}
				if (youAreNth == whosOn) {
					enableButtons();
					checkButton.setDisable(true);
				} else {
					disableButtons();
				}
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
		System.out.println("Adósságom: " + myDebt);
		// ha van adósságom
		if (myDebt.compareTo(BigDecimal.ZERO) > 0) {
			checkButton.setDisable(true);
		} else {
			callButton.setDisable(true);
		}
		//whosOn %= players;
	}

	private void incrementWhosOn() {
		++whosOn;
		whosOn %= players;
	}

	private void smallBlind() {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(2));
		myDebt = myDebt.add(amount);
		alreadInPot = alreadInPot.add(amount);
		sendPlayerCommand(HoldemPlayerCommandType.BLIND, amount, null);
	}

	private void bigBlind() {
//		myDebt = myDebt.add(pokerTable.getDefaultPot());
		alreadInPot = alreadInPot.add(pokerTable.getDefaultPot());
		sendPlayerCommand(HoldemPlayerCommandType.BLIND, pokerTable.getDefaultPot(), null);
	}

	private void printHouseCommand(HouseHoldemCommand command) {
		System.out.println("----------------");
		System.out.println(command);
	}

	private void sendPlayerCommand(HoldemPlayerCommandType type, BigDecimal callAmount, BigDecimal raiseAmount) {
		new Thread() {

			@Override
			public void run() {
				try {
					PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(type, callAmount, raiseAmount);
					model.sendCommandToTable(pokerTable, commController, playerHoldemCommand);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
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
		callButton.setDisable(false);
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
		players = houseHoldemCommand.getPlayers();
		// első körben az a dealer, aki elsőként csatlakozott, roundonként +1
		dealer = houseHoldemCommand.getDealer();
		System.out.println("Hanyadik vagy a körben: " + youAreNth);
		System.out.println("Aktuális dealer: " + dealer);
		if (areYouTheSmallBlind()) {
			System.out.println("Betettem a kis vakot");
			smallBlind();
		} else if (areYouTheBigBlind()) {
			System.out.println("Betettem a nagy vakot");
			bigBlind();
		}
		// nagyvaktól eggyel balra ülő kezd
		whosOn = ((dealer + 3) % players);
		System.out.println("Az éppen soron levő játékos: " + whosOn);
	}
	
	/**
	 * A dealer mellett közvetlenül balra ülő játékos köteles kis vakot betenni.
	 * @return ha nekem kell betenni a kis vakot, akkor true, különben false.
	 */
	private boolean areYouTheSmallBlind() {
		return youAreNth == ((dealer + 1) % players);
	}
	
	/**
	 * A dealer mellett kettővel balra ülő játékos köteles nagy vakot betenni.
	 * @return ha nekem kell betenni a nagy vakot, akkor true, különben false.
	 */
	private boolean areYouTheBigBlind() {
		return youAreNth == ((dealer + 2) % players);
	}

	private void player() {
		System.out.println(youAreNth + " " + whosOn);
		if (youAreNth == whosOn) {
			if (myDebt.compareTo(BigDecimal.ZERO) > 0) {
				enableButtons();
				checkButton.setDisable(true);
			}
		} else {
			disableButtons();
		}
	}

	/**
	 * A <b>CALL</b> gomb click handlerje
	 * @param event az esemény
	 */
	@FXML protected void handleCall(ActionEvent event) {
		BigDecimal amount = BigDecimal.ZERO.add(myDebt);
		myDebt = myDebt.subtract(amount);
		sendPlayerCommand(HoldemPlayerCommandType.CALL, BigDecimal.ZERO.add(amount), null);
	}

	@FXML protected void handleCheck(ActionEvent event) {
		sendPlayerCommand(HoldemPlayerCommandType.CHECK, null, null);
	}

	@FXML protected void handleRaise(ActionEvent event) {
		BigDecimal amount = new BigDecimal(6);
		alreadInPot = alreadInPot.add(myDebt).add(amount);
		sendPlayerCommand(HoldemPlayerCommandType.RAISE, myDebt, amount);
	}

	@FXML protected void handleFold(ActionEvent event) {
		sendPlayerCommand(HoldemPlayerCommandType.FOLD, null, null);
	}

	@FXML protected void handleQuit(ActionEvent event) {
		sendPlayerCommand(HoldemPlayerCommandType.QUIT, null, null);
		frameController.setTableListerFXML();
	}
}
