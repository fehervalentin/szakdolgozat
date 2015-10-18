package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.Scanner;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerObserverController;
import hu.elte.bfw1p6.poker.client.model.MainGameModel;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;
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

	private int whosOn = 0;

	private int youAreNth;

	private int players;
	
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
		if (updateMsg instanceof HouseHoldemCommand) {
			HouseHoldemCommand houseHoldemCommand = (HouseHoldemCommand)updateMsg;
			if (houseHoldemCommand.getHouseCommandType() == HoldemHouseCommandType.PLAYER) {
				System.out.println("----------------");
				System.out.println(houseHoldemCommand.getHouseCommandType());
				System.out.println(houseHoldemCommand.getCard1());
				System.out.println(houseHoldemCommand.getCard2());
				System.out.println(houseHoldemCommand.getNthPlayer());
				youAreNth = houseHoldemCommand.getNthPlayer();
				players = houseHoldemCommand.getPlayers();
				if (youAreNth == whosOn) {
					//					youAreNext();
					System.out.println("Te jössz!");
				}
			} else if (houseHoldemCommand.getHouseCommandType() == HoldemHouseCommandType.FLOP) {
				System.out.println("----------------");
				System.out.println(houseHoldemCommand.getHouseCommandType());
				System.out.println(houseHoldemCommand.getCard1());
				System.out.println(houseHoldemCommand.getCard2());
				System.out.println(houseHoldemCommand.getCard3());
				if (youAreNth == whosOn) {
					//					youAreNext();
					System.out.println("Te jössz!");
				}
				//youAreNext();
			} else if (houseHoldemCommand.getHouseCommandType() == HoldemHouseCommandType.TURN) {
				System.out.println("----------------");
				System.out.println(houseHoldemCommand.getHouseCommandType());
				System.out.println(houseHoldemCommand.getCard1());
				if (youAreNth == whosOn) {
					//					youAreNext();
					System.out.println("Te jössz!");
				}
			} else if (houseHoldemCommand.getHouseCommandType() == HoldemHouseCommandType.RIVER) {
				System.out.println("----------------");
				System.out.println(houseHoldemCommand.getHouseCommandType());
				System.out.println(houseHoldemCommand.getCard1());
				if (youAreNth == whosOn) {
					//					youAreNext();
					System.out.println("Te jössz!");
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
				youAreNext();
			}
			if (playerHoldemCommand.getPlayerCommandType() != HoldemPlayerCommandType.QUIT) {
			}
		}
		whosOn++;
		whosOn %= players;
	}

	public void youAreNext() {
		System.out.println("Te jössz: ");
		//		String command = "CHECK";//System.console().readLine();
		Scanner in = new Scanner(System.in);
		String command = in.next();
		String amount = "0";
		if (HoldemPlayerCommandType.valueOf(command) == HoldemPlayerCommandType.RAISE) {
			System.out.println("Mennyit: ");
			amount = in.next();
		}
		in.close();
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.valueOf(command), BigDecimal.valueOf(Double.valueOf(amount)));

		new Thread() {

			@Override
			public void run() {
				try {
					model.sendCommandToTable(pokerTable, commController, playerHoldemCommand);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (PokerUnauthenticatedException e) {
					errorAlert.setContentText(e.getMessage());
					errorAlert.showAndWait();
				}
			}}.start();

	}

	@FXML protected void handleCheck(ActionEvent event) {
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(HoldemPlayerCommandType.CHECK, null);
		new Thread() {

			@Override
			public void run() {
				try {
					model.sendCommandToTable(pokerTable, commController, playerHoldemCommand);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (PokerUnauthenticatedException e) {
					errorAlert.setContentText(e.getMessage());
					errorAlert.showAndWait();
				}
			}}.start();
	}

	@FXML protected void handleRaise(ActionEvent event) {
	}

	@FXML protected void handleFold(ActionEvent event) {
	}

	@FXML protected void handleQuit(ActionEvent event) {
	}
}
