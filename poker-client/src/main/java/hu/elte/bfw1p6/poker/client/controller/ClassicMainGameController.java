package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.ClassicMainGameModel;
import hu.elte.bfw1p6.poker.client.view.ClassicMainView;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ClassicMainGameController extends AbstractMainGameController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorAlert = new Alert(AlertType.ERROR);
		mainView = new ClassicMainView(mainGamePane);

		try {
			commController = new CommunicatorController(this);
			model = new ClassicMainGameModel(commController);
			model.connectToTable(commController);
			pokerLabel.setText(model.getUserName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PokerTooMuchPlayerException e) {
			showErrorAlert(e.getMessage());
			frameController.setTableListerFXML();
		} catch (PokerUnauthenticatedException e) {
			showErrorAlert(e.getMessage());
			frameController.setLoginFXML();
		}
		modifyButtonsDisability(true);
	}

	@Override
	public void updateMe(Object updateMsg) {
		if (updateMsg instanceof ClassicHouseCommand) {
			ClassicHouseCommand classicHouseCommand = (ClassicHouseCommand)updateMsg;
			System.out.println("A haz utasítást küldött: " + classicHouseCommand.getHouseCommandType());

			switch (classicHouseCommand.getHouseCommandType()) {
			case BLIND: {
				receivedBlindHouseCommand(classicHouseCommand);
				break;
			}
			case DEAL: {
				receivedDealHouseCommand(classicHouseCommand);
				break;
			}
			case BET: {
				receivedBetHouseCommand(classicHouseCommand);
				break;
			}
			case CHANGE: {
				receivedChangeHouseCommand(classicHouseCommand);
				break;
			}
			case DEAL2: {
				receivedDeal2HouseCommand(classicHouseCommand);
				break;
			}
			case BET2: {
				receivedBet2HouseCommand(classicHouseCommand);
				break;
			}
			case WINNER: {
				receivedWinnerHouseCommand(classicHouseCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof HoldemPlayerCommand) {
			ClassicPlayerCommand playerHoldemCommand = (ClassicPlayerCommand)updateMsg;
			//			System.out.println("Ki kuldte a player commandot: " + playerHoldemCommand.getSender() + "\nMilyen command: " + playerHoldemCommand.getPlayerCommandType());
			//			System.out.println("You are nth: " + model.getYouAreNth() + " Whoson: " + playerHoldemCommand.getWhosOn());
			System.out.println("A(z) " + playerHoldemCommand.getSender() + " játékos utasítást küldött: " + playerHoldemCommand.getPlayerCommandType());

			switch (playerHoldemCommand.getPlayerCommandType()) {
			case BLIND: {
				receivedBlindPlayerCommand(playerHoldemCommand);
				break;
			}
			case CALL: {
				receivedCallPlayerCommand(playerHoldemCommand);
				break;
			}
			case CHECK: {
				receivedCheckPlayerCommand(playerHoldemCommand);
				break;
			}
			case FOLD: {
				receivedFoldPlayerCommand(playerHoldemCommand);
				//				modifyButtonVisibilities(playerHoldemCommand);
				break;
			}
			case RAISE: {
				System.out.println("A RAISE mértéke: " + playerHoldemCommand.getRaiseAmount());
				receivedRaisePlayerCommand(playerHoldemCommand);
				break;
			}
			case QUIT: {
				receivedQuitPlayerCommand(playerHoldemCommand);
				break;
			}
			case CHANGE: {
				break;
			}
			default: {
				break;
			}
			}
			modifyButtonVisibilities(playerHoldemCommand);
		} else {
			throw new IllegalArgumentException();
		}
		//		System.out.println("Adósságom: " + model.getMyDebt());
		// ha van adósságom
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

	private void receivedBetHouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonVisibilities(classicHouseCommand);
		((ClassicMainView)mainView).receivedBetHouseCommand(classicHouseCommand);
	}

	private void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonVisibilities(classicHouseCommand);
		((ClassicMainView)mainView).receivedChangeHouseCommand(classicHouseCommand);
	}

	private void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonVisibilities(classicHouseCommand);
		((ClassicMainView)mainView).receivedDeal2HouseCommand(classicHouseCommand);
	}

	private void receivedBet2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonVisibilities(classicHouseCommand);
		((ClassicMainView)mainView).receivedBet2HouseCommand(classicHouseCommand);
	}

}
