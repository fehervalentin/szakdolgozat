package hu.elte.bfw1p6.poker.client.controller.game;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.ClassicMainGameModel;
import hu.elte.bfw1p6.poker.client.view.ClassicMainGameView;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

/**
 * A classic játékmód kliens oldali controllere.
 * @author feher
 *
 */
public class ClassicMainGameController extends AbstractMainGameController {
	
	@FXML protected Button changeButton;

	@Override
	public synchronized void initialize(URL location, ResourceBundle resources) {
		this.mainView = new ClassicMainGameView(mainGamePane);
		
		try {
			this.commController = new CommunicatorController(this);
			this.model = new ClassicMainGameModel(commController);
			this.model.connectToTable(commController);
			this.mainView.setUserName(model.getUserName());
			this.mainView.setBalance(model.getBalance());
			
			//ha bármilyen bug esetén a QUIT gomb letiltva marad, akkor az asztalt nem lehet elhagyni...
			mainGamePane.setOnKeyPressed(key -> {
				if (key.getCode() == KeyCode.Q) {
					frameController.setTableListerFXML();
				}
			});
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
		setButtonsDisability(true);
		setChangeButtonDisability(true);
		modifyQuitButtonDisability(false);
		setTextArea();
		synchronized (textArea) {
			Platform.runLater(() -> textArea.appendText("Felhasználónevem: " + model.getUserName()));
		}
	}

	/**
	 * Módosítja a CHANGE gomb disable tulajdonságát.
	 * @param disabled a beállítandó érték
	 */
	private void setChangeButtonDisability(boolean disabled) {
		changeButton.setDisable(disabled);
	}

	@Override
	public void update(Object updateMsg) {
		int whosOn = -1;
		if (updateMsg instanceof ClassicHouseCommand) {
			ClassicHouseCommand classicHouseCommand = (ClassicHouseCommand)updateMsg;
			whosOn = classicHouseCommand.getWhosOn();
			logHouseCommand(classicHouseCommand);
			setButtonsDisability(model.getYouAreNth() != classicHouseCommand.getWhosOn());

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
			ClassicPlayerCommand classicPlayerCommand = (ClassicPlayerCommand)updateMsg;
			whosOn = classicPlayerCommand.getWhosOn();
			logPlayerCommand(classicPlayerCommand);
			setButtonsDisability(model.getYouAreNth() !=  classicPlayerCommand.getWhosOn());

			switch (classicPlayerCommand.getPlayerCommandType()) {
			case BLIND: {
				receivedBlindPlayerCommand(classicPlayerCommand);
				break;
			}
			case CALL: {
				receivedCallPlayerCommand(classicPlayerCommand);
				break;
			}
			case CHECK: {
				receivedCheckPlayerCommand(classicPlayerCommand);
				break;
			}
			case FOLD: {
				receivedFoldPlayerCommand(classicPlayerCommand);
				break;
			}
			case RAISE: {
				receivedRaisePlayerCommand(classicPlayerCommand);
				break;
			}
			case QUIT: {
				receivedQuitPlayerCommand(classicPlayerCommand);
				break;
			}
			case CHANGE: {
				receivedChangePlayerCommand(classicPlayerCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
		} else if (updateMsg instanceof String){
			String msg = (String)updateMsg;
			if (!msg.equals("ping")) {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
		debtChecker(whosOn);
	}

	/**
	 * A CHANGE gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleChange(ActionEvent event) {
		try {
			setChangeButtonDisability(true);
			((ClassicMainGameModel)model).sendChangeCommand(((ClassicMainGameView)mainView).getMarkedCards());
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A szerver CHANGE típusú utasítást küldött.
	 * @param classicHouseCommand az utasítás
	 */
	private void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		((ClassicMainGameView)mainView).receivedChangeHouseCommand(classicHouseCommand);
		changeState(classicHouseCommand.getWhosOn());
	}

	/**
	 * A szerver DEAL2 típusú utasítást küldött.
	 * @param classicHouseCommand az utasítás
	 */
	private void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		((ClassicMainGameModel)model).receivedDeal2HouseCommand(classicHouseCommand);
		((ClassicMainGameView)mainView).receivedDeal2HouseCommand(classicHouseCommand);
		changeButton.setDisable(true);
	}

	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param classicPlayerCommand az utasítás
	 */
	private void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		((ClassicMainGameView)mainView).receivedChangePlayerCommand(classicPlayerCommand);
		changeState(classicPlayerCommand.getWhosOn());
	}
	
	/**
	 * Ha CHANGE típusú utasítás érkezett, akkor a gombokat ennek megfelelően kell beállítani.
	 * @param pokerCommand
	 */
	private void changeState(int whosOn) {
		if (model.getYouAreNth() == whosOn) {
			setButtonsDisability(true);
			setChangeButtonDisability(false);
			modifyQuitButtonDisability(false);
		}
	}
}