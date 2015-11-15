package hu.elte.bfw1p6.poker.client.controller.game;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.TimerTask;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.HoldemMainGameModel;
import hu.elte.bfw1p6.poker.client.view.HoldemMainView;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;

/**
 * A holdem játékmód kliens oldali controllere.
 * @author feher
 *
 */
public class HoldemMainGameController extends AbstractMainGameController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.mainView = new HoldemMainView(mainGamePane);
//		this.automateExecution = new Timer();

		try {
			this.commController = new CommunicatorController(this);
			this.model = new HoldemMainGameModel(commController);
			this.model.connectToTable(commController);
			this.mainView.setUserName(model.getUserName());
			this.pokerLabel.setText(model.getUserName());
		} catch (PokerTooMuchPlayerException e) {
			showErrorAlert(e.getMessage());
			frameController.setTableListerFXML();
		} catch (PokerUnauthenticatedException | RemoteException e) {
			remoteExceptionHandler();
		}
		setButtonsDisability(true);
		setQuitButtonDisability(false);
	}

	@Override
	public void update(Object updateMsg) throws RemoteException {
		// ha a ház küld utasítást
		int whosOn = -1;
		if (updateMsg instanceof HoldemHouseCommand) {
			HoldemHouseCommand houseHoldemCommand = (HoldemHouseCommand)updateMsg;
			whosOn = houseHoldemCommand.getWhosOn();
			System.out.println("A haz utasítást küldött: " + houseHoldemCommand.getHouseCommandType());
			setButtonsDisability(model.getYouAreNth() != houseHoldemCommand.getWhosOn());
			
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
			case WINNER: {
				receivedWinnerHouseCommand(houseHoldemCommand);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
			}
//			if (houseHoldemCommand.getHouseCommandType() != HoldemHouseCommandType.BLIND) {
//				if(houseHoldemCommand.getWhosOn() == mainView.getYouAreNth()) {
//					timerTask = createTimerTask();
//					automateExecution.schedule(timerTask, delay);
//				}
//			}
		} else if (updateMsg instanceof HoldemPlayerCommand) {
			HoldemPlayerCommand holdemPlayerCommand = (HoldemPlayerCommand)updateMsg;
			whosOn = holdemPlayerCommand.getWhosOn();
			System.out.println(holdemPlayerCommand.getSender() + " játékos utasítást küldött: " + holdemPlayerCommand.getPlayerCommandType());
			setButtonsDisability(model.getYouAreNth() !=  holdemPlayerCommand.getWhosOn());
			
			switch (holdemPlayerCommand.getPlayerCommandType()) {
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
				break;
			}
			case RAISE: {
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
//			if (holdemPlayerCommand.getPlayerCommandType() != HoldemPlayerCommandType.BLIND && holdemPlayerCommand.getPlayerCommandType() != HoldemPlayerCommandType.FOLD) {
//				System.out.println("Sajatom: " + holdemPlayerCommand.getWhosOn() + " " + mainView.getYouAreNth());
//				if(holdemPlayerCommand.getWhosOn() == mainView.getYouAreNth()) {
//					System.out.println("Új taszkot hoztam létre!");
//					timerTask = createTimerTask();
//					automateExecution.schedule(timerTask, delay);
//				}
//			}
			/*if (!holdemPlayerCommand.isWinnerCommand() && !holdemPlayerCommand.getCommandType().equals("QUIT") || holdemPlayerCommand.getCommandType().equals("QUIT") && holdemPlayerCommand.getClientsCount() >=2) {
				modifyButtonsDisability(holdemPlayerCommand);
			}*/
		} else {
			throw new IllegalArgumentException();
		}
		debtChecker(whosOn);
	}
	
	@Override
	protected TimerTask createTimerTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
				if (!checkButton.isDisable()) {
					checkButton.fire();
				} else {
					foldButton.fire();
				}
			}
		};
	}

	/**
	 * A szerver FLOP típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private void receivedFlopHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		Card[] cards =  houseHoldemCommand.getCards();
		System.out.println("Flop: " + cards[0] + " " + cards[1] + " " + cards[2]);
		((HoldemMainView)mainView).receivedFlopHouseCommand(houseHoldemCommand);
	}

	/**
	 * A szerver TURN típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private void receivedTurnHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		System.out.println("Turn: " + houseHoldemCommand.getCards()[0]);
		((HoldemMainView)mainView).receivedTurnHouseCommand(houseHoldemCommand);
	}

	/**
	 * A szerver RIVER típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private void receivedRiverHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		System.out.println("River: " + houseHoldemCommand.getCards()[0]);
		((HoldemMainView)mainView).receivedRiverHouseCommand(houseHoldemCommand);
	}
}