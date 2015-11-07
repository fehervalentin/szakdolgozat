package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.ClassicMainGameModel;
import hu.elte.bfw1p6.poker.client.view.ClassicMainView;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class ClassicMainGameController extends AbstractMainGameController {

	@FXML protected Button changeButton;

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
		modifyButtonsDisability(null);
		modifyChangeButtonDisability(true);
	}

	private void modifyChangeButtonDisability(boolean enabled) {
		changeButton.setDisable(enabled);
	}

	@Override
	public void updateMe(Object updateMsg) {
		if (updateMsg instanceof ClassicHouseCommand) {
			ClassicHouseCommand classicHouseCommand = (ClassicHouseCommand)updateMsg;
			System.out.println("A ház utasítást küldött: " + classicHouseCommand.getHouseCommandType());

			switch (classicHouseCommand.getHouseCommandType()) {
			case BLIND: {
				receivedBlindHouseCommand(classicHouseCommand);
				break;
			}
			case DEAL: {
				receivedDealHouseCommand(classicHouseCommand);
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
			case WINNER: {
				receivedWinnerHouseCommand(classicHouseCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof ClassicPlayerCommand) {
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
				receivedChangePlayerCommand(playerHoldemCommand);
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

	@FXML protected void handleChange(ActionEvent event) {
		List<Integer> markedCards = ((ClassicMainView)mainView).getMarkedCards();
		try {
			modifyChangeButtonDisability(true);
			((ClassicMainGameModel)model).change(markedCards);
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	private void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonsDisability(classicHouseCommand);
		((ClassicMainView)mainView).receivedChangeHouseCommand(classicHouseCommand);
		modifyButtonsDisability(null);
		modifyChangeButtonDisability(model.getYouAreNth() != classicHouseCommand.getWhosOn());
		modifyFoldButtonDisability(true);
	}

	private void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		modifyButtonsDisability(classicHouseCommand);
		((ClassicMainGameModel)model).receivedDeal2HouseCommand(classicHouseCommand);
		((ClassicMainView)mainView).receivedDeal2HouseCommand(classicHouseCommand);
	}

	private void receivedChangePlayerCommand(ClassicPlayerCommand playerHoldemCommand) {
		modifyButtonsDisability(playerHoldemCommand);
		modifyButtonsDisability(null);
		modifyChangeButtonDisability(model.getYouAreNth() != playerHoldemCommand.getWhosOn());
		modifyFoldButtonDisability(true);
		((ClassicMainView)mainView).receivedChangePlayerCommand(playerHoldemCommand);
	}
}
