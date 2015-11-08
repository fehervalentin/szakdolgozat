package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.HoldemMainGameModel;
import hu.elte.bfw1p6.poker.client.view.HoldemMainView;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class HoldemMainGameController extends AbstractMainGameController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorAlert = new Alert(AlertType.ERROR);
		mainView = new HoldemMainView(mainGamePane);

		try {
			commController = new CommunicatorController(this);
			model = new HoldemMainGameModel(commController);
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
		modifyButtonsDisability(null);
	}

	@Override
	public void updateMe(Object updateMsg) {
		// ha a ház küld utasítást
		if (updateMsg instanceof HoldemHouseCommand) {
			HoldemHouseCommand houseHoldemCommand = (HoldemHouseCommand)updateMsg;
//			printHouseCommand(houseHoldemCommand);
			System.out.println("A haz utasítást küldött: " + houseHoldemCommand.getHouseCommandType());
			
			switch (houseHoldemCommand.getHouseCommandType()) {
			case BLIND: {
				receivedBlindHouseCommand(houseHoldemCommand);
				break;
			}
			case DEAL: {
				receivedDealHouseCommand(houseHoldemCommand);
				break;
			}
			case FLOP: {
				Card[] cards = houseHoldemCommand.getCards();
				System.out.println("Flop: " + cards[0] + " " + cards[1] + " " + cards[2]);
				receivedFlopHouseCommand(houseHoldemCommand);
				break;
			}
			case TURN: {
				Card[] cards = houseHoldemCommand.getCards();
				System.out.println("Turn: " + cards[0]);
				receivedTurnHouseCommand(houseHoldemCommand);
				break;
			}
			case RIVER: {
				Card[] cards = houseHoldemCommand.getCards();
				System.out.println("River: " + cards[0]);
				receivedRiverHouseCommand(houseHoldemCommand);
				break;
			}
			case WINNER: {
				Card[] cards = houseHoldemCommand.getCards();
				System.out.println("Winner: " + cards[0] + " " + cards[1]);
				receivedWinnerHouseCommand(houseHoldemCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof HoldemPlayerCommand) {
			HoldemPlayerCommand playerHoldemCommand = (HoldemPlayerCommand)updateMsg;
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
			default: {
				break;
			}
			}
			modifyButtonsDisability(playerHoldemCommand);
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

	private void receivedFlopHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		modifyButtonsDisability(holdemHouseCommand);
		((HoldemMainView)mainView).flop(holdemHouseCommand);
	}

	private void receivedTurnHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		modifyButtonsDisability(houseHoldemCommand);
		((HoldemMainView)mainView).turn(houseHoldemCommand);
	}

	private void receivedRiverHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		modifyButtonsDisability(houseHoldemCommand);
		((HoldemMainView)mainView).river(houseHoldemCommand);
	}
}