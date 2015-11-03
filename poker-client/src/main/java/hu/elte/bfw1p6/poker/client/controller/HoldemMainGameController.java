package hu.elte.bfw1p6.poker.client.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.model.HouseClientMainGameModel;
import hu.elte.bfw1p6.poker.client.view.MainView;
import hu.elte.bfw1p6.poker.command.api.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHousePokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerPokerCommandType;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class HoldemMainGameController extends AbstractMainGameController<HoldemPlayerPokerCommandType> {

	@FXML private AnchorPane mainGamePane;

	@FXML private Label pokerLabel;

	@FXML private Button callButton;
	@FXML private Button checkButton;
	@FXML private Button raiseButton;
	@FXML private Button foldButton;
	@FXML private Button quitButton;

	private MainView mainView;

	private HouseClientMainGameModel model;


	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
		this.scene = this.frameController.getScene();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorAlert = new Alert(AlertType.ERROR);
		mainView = new MainView(mainGamePane);

		try {
			commController = new CommunicatorController(this);
			model = new HouseClientMainGameModel(commController);
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
		// ha a ház küld utasítást
		if (updateMsg instanceof HoldemHousePokerCommand) {
			HoldemHousePokerCommand houseHoldemCommand = (HoldemHousePokerCommand)updateMsg;
//			printHouseCommand(houseHoldemCommand);
			System.out.println("A haz utasítást küldött: " + houseHoldemCommand.getType());
			
			switch (houseHoldemCommand.getType()) {
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
				System.out.println("Turn: " + houseHoldemCommand.getCards()[0]);
				receivedTurnHouseCommand(houseHoldemCommand);
				break;
			}
			case RIVER: {
				System.out.println("River: " + houseHoldemCommand.getCards()[0]);
				receivedRiverHouseCommand(houseHoldemCommand);
				break;
			}
			case WINNER: {
				Card[] cards = houseHoldemCommand.getCards();
				System.out.println("Winner: " + houseHoldemCommand.getWinner() + " " + cards[0] + " " + cards[1]);
				receivedWinnerHouseCommand(houseHoldemCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof HoldemPlayerPokerCommand) {
			HoldemPlayerPokerCommand holdemPlayerCommand = (HoldemPlayerPokerCommand)updateMsg;
//			System.out.println("Ki kuldte a player commandot: " + playerHoldemCommand.getSender() + "\nMilyen command: " + playerHoldemCommand.getPlayerCommandType());
//			System.out.println("You are nth: " + model.getYouAreNth() + " Whoson: " + playerHoldemCommand.getWhosOn());
			System.out.println("A(z) " + holdemPlayerCommand.getSender() + " játékos utasítást küldött: " + holdemPlayerCommand.getType());
			
			switch (holdemPlayerCommand.getType().getActual()) {
			case BLIND: {
				receivedBlindPlayerCommand(holdemPlayerCommand);
				break;
			}
			case CALL: {
				receivedCallPlayerCommand(holdemPlayerCommand);
				break;
			}
			case CHECK: {
				receivedCheckPlayerCommand(holdemPlayerCommand);
				break;
			}
			case FOLD: {
				receivedFoldPlayerCommand(holdemPlayerCommand);
//				modifyButtonVisibilities(playerHoldemCommand);
				break;
			}
			case RAISE: {
				System.out.println("A RAISE mértéke: " + holdemPlayerCommand.getRaiseAmount());
				receivedRaisePlayerCommand(holdemPlayerCommand);
				break;
			}
			case QUIT: {
				receivedQuitPlayerCommand(holdemPlayerCommand);
				break;
			}
			default: {
				break;
			}
			}
			modifyButtonVisibilities(holdemPlayerCommand);
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

	private void receivedBlindHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		try {
			mainView.receivedBlindHouseCommand(houseHoldemCommand);
			model.receivedBlindHouseCommand(houseHoldemCommand);
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	private void receivedDealHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		model.receivedPlayerHouseCommand(houseHoldemCommand);
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.receivedPlayerHouseCommand(houseHoldemCommand);
	}

	private void receivedFlopHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		System.out.println("Flop kommand: " + model.getYouAreNth() + "  " + houseHoldemCommand.getWhosOn());
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.flop(houseHoldemCommand);
	}

	private void receivedTurnHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.turn(houseHoldemCommand);
	}

	private void receivedRiverHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.river(houseHoldemCommand);
	}

	private void receivedWinnerHouseCommand(HoldemHousePokerCommand houseHoldemCommand) {
		mainView.winner(houseHoldemCommand);
	}


	
	

	private void receivedBlindPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedBlindPlayerCommand(playerHoldemCommand);
		mainView.receivedBlindPlayerCommand(playerHoldemCommand);
	}

	private void receivedCallPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedCallPlayerCommand(playerHoldemCommand);
		mainView.receivedCallPlayerCommand(playerHoldemCommand);
	}

	private void receivedCheckPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedCheckPlayerCommand(playerHoldemCommand);
		mainView.receivedCheckPlayerCommand(playerHoldemCommand);
	}

	private void receivedFoldPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedFoldPlayerCommand(playerHoldemCommand);
		mainView.receivedFoldPlayerCommand(playerHoldemCommand);
	}

	private void receivedRaisePlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedRaisePlayerCommand(playerHoldemCommand);
		mainView.receivedRaisePlayerCommand(playerHoldemCommand);
		checkButton.setDisable(true);
	}

	private void receivedQuitPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		model.receivedQuitPlayerCommand(playerHoldemCommand);
		mainView.receivedQuitPlayerCommand(playerHoldemCommand);
	}










	private void modifyButtonVisibilities(PokerCommand pokerCommand) {
		if (pokerCommand instanceof HoldemHousePokerCommand) {
			pokerCommand = (HoldemHousePokerCommand)pokerCommand;
		} else if (pokerCommand instanceof HoldemPlayerPokerCommand) {
			pokerCommand = (HoldemPlayerPokerCommand)pokerCommand;
		}
		boolean disable = model.getYouAreNth() == pokerCommand.getWhosOn() ? false : true;
		System.out.println("Flop kommandban button disability: " + model.getYouAreNth() + " " + pokerCommand.getWhosOn());
		modifyButtonsDisability(disable);
	}

	private void modifyButtonsDisability(boolean disable) {
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

	private void player(HoldemHousePokerCommand houseHoldemCommand) {
		boolean disable = model.getYouAreNth() == houseHoldemCommand.getWhosOn() ? false : true;
		modifyButtonsDisability(disable);
		if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
			checkButton.setDisable(true);
		}
	}

	private void printHouseCommand(HoldemHousePokerCommand command) {
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
		frameController.setMainGameFXML();
		//		frameController.setTableListerFXML();
	}

	private void showErrorAlert(String msg) {
		errorAlert.setContentText(msg);
		errorAlert.showAndWait();
	}
}