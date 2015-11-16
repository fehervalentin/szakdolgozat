package hu.elte.bfw1p6.poker.client.controller.game;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimerTask;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.ClassicMainGameModel;
import hu.elte.bfw1p6.poker.client.view.ClassicMainView;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * A classic játékmód kliens oldali controllere.
 * @author feher
 *
 */
public class ClassicMainGameController extends AbstractMainGameController {
	
	@FXML protected Button changeButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		textArea.setDisable(false);
		this.mainView = new ClassicMainView(mainGamePane);
//		this.automateExecution = new Timer();

		try {
			commController = new CommunicatorController(this);
			model = new ClassicMainGameModel(commController);
			model.connectToTable(commController);
		} catch (RemoteException e) {
			remoteExceptionHandler();
		} catch (PokerTooMuchPlayerException e) {
			showErrorAlert(e.getMessage());
			frameController.setTableListerFXML();
		}
		setButtonsDisability(true);
		setChangeButtonDisability(true);
		setQuitButtonDisability(false);
	}

	/**
	 * Módosítja a CHANGE button disable tulajdonságát.
	 * @param disabled
	 */
	private void setChangeButtonDisability(boolean enabled) {
		changeButton.setDisable(enabled);
	}

	@Override
	protected TimerTask createTimerTask() {
		return new TimerTask() {

			@Override
			public void run() {
				if (!checkButton.isDisable()) {
					checkButton.fire();
				} else if (!changeButton.isDisable()) {
					changeButton.fire();
				} else {
					foldButton.fire();
				}
			}
		};
	}

	@Override
	public void update(Object updateMsg) {
		int whosOn = -1;
		if (updateMsg instanceof ClassicHouseCommand) {
			ClassicHouseCommand classicHouseCommand = (ClassicHouseCommand)updateMsg;
			whosOn = classicHouseCommand.getWhosOn();
			System.out.println("A ház utasítást küldött: " + classicHouseCommand.getHouseCommandType());
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
			System.out.println(classicPlayerCommand.getSender() + " játékos utasítást küldött: " + classicPlayerCommand.getPlayerCommandType());
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
				break;
			}
			}
//			if (classicPlayerCommand.getPlayerCommandType() != ClassicPlayerCommandType.BLIND) {
//				if(classicPlayerCommand.getWhosOn() == mainView.getYouAreNth()) {
//					automateExecution.purge();
//					timerTask = createTimerTask();
//					automateExecution.schedule(timerTask, delay);
//				}
//			}
			/*if (!classicPlayerCommand.isWinnerCommand()  && !classicPlayerCommand.getCommandType().equals("QUIT") || classicPlayerCommand.getCommandType().equals("QUIT") && classicPlayerCommand.getClientsCount() >=2) {
				modifyButtonsDisability(classicPlayerCommand);
			}*/
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
//		timerTask.cancel();
		List<Integer> markedCards = ((ClassicMainView)mainView).getMarkedCards();
		try {
			setChangeButtonDisability(true);
			((ClassicMainGameModel)model).sendChangeCommand(markedCards);
		} catch (PokerDataBaseException | PokerUserBalanceException e) {
			showErrorAlert(e.getMessage());
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
	}

	/**
	 * A szerver CHANGE típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	private void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		((ClassicMainView)mainView).receivedChangeHouseCommand(classicHouseCommand);
		changeState(classicHouseCommand);
	}

	/**
	 * A szerver DEAL2 típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	private void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		((ClassicMainGameModel)model).receivedDeal2HouseCommand(classicHouseCommand);
		((ClassicMainView)mainView).receivedDeal2HouseCommand(classicHouseCommand);
		changeButton.setDisable(true);
	}

	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param houseCommand az utasítás
	 */
	private void receivedChangePlayerCommand(ClassicPlayerCommand playerHoldemCommand) {
		((ClassicMainView)mainView).receivedChangePlayerCommand(playerHoldemCommand);
		changeState(playerHoldemCommand);
	}
	
	private void changeState(PokerCommand pokerCommand) {
		if (model.getYouAreNth() == pokerCommand.getWhosOn()) {
			setButtonsDisability(true);
			setChangeButtonDisability(false);
			setQuitButtonDisability(false);
		}
	}
}