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
import hu.elte.bfw1p6.poker.client.view.MainView;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainGameController implements Initializable, PokerClientController, PokerObserverController {

	@FXML private AnchorPane mainGamePane;

	@FXML private Label pokerLabel;

	@FXML private Button callButton;
	@FXML private Button checkButton;
	@FXML private Button raiseButton;
	@FXML private Button foldButton;
	@FXML private Button quitButton;

	private MainView mainView;

	private MainGameModel model;

	private FrameController frameController;

	private CommunicatorController commController;

	private Scene scene;

	private Alert errorAlert;


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
			model = new MainGameModel(commController);
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
		if (updateMsg instanceof HouseHoldemCommand) {
			HouseHoldemCommand houseHoldemCommand = (HouseHoldemCommand)updateMsg;
//			printHouseCommand(houseHoldemCommand);

			switch (houseHoldemCommand.getHouseCommandType()) {
			case BLIND: {
				receivedBlindHouseCommand(houseHoldemCommand);
				break;
			}
			case PLAYER: {
				receivedPlayerHouseCommand(houseHoldemCommand);
				break;
			}
			case FLOP: {
				receivedFlopHouseCommand(houseHoldemCommand);
				break;
			}
			case TURN: {
				receivedTurnHouseCommand(houseHoldemCommand);
				break;
			}
			case RIVER: {
				receivedRiverHouseCommand(houseHoldemCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof PlayerHoldemCommand) {
			PlayerHoldemCommand playerHoldemCommand = (PlayerHoldemCommand)updateMsg;
//			System.out.println("Ki kuldte a player commandot: " + playerHoldemCommand.getSender() + "\nMilyen command: " + playerHoldemCommand.getPlayerCommandType());
//			System.out.println("You are nth: " + model.getYouAreNth() + " Whoson: " + playerHoldemCommand.getWhosOn());
			modifyButtonVisibilities(playerHoldemCommand);

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
				break;
			}
			case RAISE: {
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
		} else {
			throw new IllegalArgumentException();
		}
//		System.out.println("Adósságom: " + model.getMyDebt());
		// ha van adósságom
		if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
			checkButton.setDisable(true);
		} else {
			callButton.setDisable(true);
		}
	}



	private void receivedBlindHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		System.out.println("A haz utasítást küldött: " + houseHoldemCommand.getHouseCommandType());
		try {
			mainView.receivedBlindHouseCommand(houseHoldemCommand);
			model.receivedBlindHouseCommand(houseHoldemCommand);
		} catch (PokerUnauthenticatedException | PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		}
	}

	private void receivedPlayerHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		System.out.println("A haz utasítást küldött: " + houseHoldemCommand.getHouseCommandType());
		model.player(houseHoldemCommand);
		modifyButtonVisibilities(houseHoldemCommand);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				mainView.player(houseHoldemCommand);
			}
		});
	}

	private void receivedFlopHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.flop(houseHoldemCommand);
	}

	private void receivedTurnHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.turn(houseHoldemCommand);
	}

	private void receivedRiverHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		modifyButtonVisibilities(houseHoldemCommand);
		mainView.river(houseHoldemCommand);
	}



	private void receivedBlindPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		System.out.println("A(z) " + playerHoldemCommand.getSender() + " játékos utasítást küldött: " + playerHoldemCommand.getPlayerCommandType());
		model.receivedBlindPlayerCommand(playerHoldemCommand);
		mainView.receivedBlindPlayerCommand(playerHoldemCommand);
		//TODO: modelben elvileg semmi, megjelenítésben pedig vakot kell berakni az asztalra...
		//TODO: meg kiírni hogy most már mennyi lett a pot
	}

	private void receivedCallPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		model.receivedCallPlayerCommand(playerHoldemCommand);
		mainView.receivedCallPlayerCommand(playerHoldemCommand);
	}

	private void receivedCheckPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		model.receivedCheckPlayerCommand(playerHoldemCommand);
		mainView.receivedCheckPlayerCommand(playerHoldemCommand);
	}

	private void receivedFoldPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		model.receivedFoldPlayerCommand(playerHoldemCommand);
		mainView.receivedFoldPlayerCommand(playerHoldemCommand);
	}

	private void receivedRaisePlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		model.receivedRaisePlayerCommand(playerHoldemCommand);
		mainView.receivedRaisePlayerCommand(playerHoldemCommand);
		checkButton.setDisable(true);
	}

	private void receivedQuitPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		model.receivedQuitPlayerCommand(playerHoldemCommand);
		mainView.receivedQuitPlayerCommand(playerHoldemCommand);
	}










	private void modifyButtonVisibilities(PokerCommand pokerCommand) {
		if (pokerCommand instanceof HouseHoldemCommand) {
			pokerCommand = (HouseHoldemCommand)pokerCommand;
		} else if (pokerCommand instanceof PlayerHoldemCommand) {
			pokerCommand = (PlayerHoldemCommand)pokerCommand;
		}
		boolean disable = model.getYouAreNth() == pokerCommand.getWhosOn() ? false : true;
		modifyButtonsDisability(disable);
	}

	private void modifyButtonsDisability(boolean disable) {
		callButton.setDisable(disable);
		checkButton.setDisable(disable);
		foldButton.setDisable(disable);
		raiseButton.setDisable(disable);
	}

	private void player(HouseHoldemCommand houseHoldemCommand) {
		boolean disable = model.getYouAreNth() == houseHoldemCommand.getWhosOn() ? false : true;
		modifyButtonsDisability(disable);
		if (model.getMyDebt().compareTo(BigDecimal.ZERO) > 0) {
			checkButton.setDisable(true);
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