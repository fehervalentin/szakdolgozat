package hu.elte.bfw1p6.poker.client.controller.game;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.HoldemMainGameModel;
import hu.elte.bfw1p6.poker.client.view.HoldemMainGameView;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

/**
 * A holdem játékmód kliens oldali controllere.
 * @author feher
 *
 */
public class HoldemMainGameController extends AbstractMainGameController {

	@Override
	public synchronized void initialize(URL location, ResourceBundle resources) {
		this.mainView = new HoldemMainGameView(mainGamePane);

		try {
			this.commController = new CommunicatorController(this);
			this.model = new HoldemMainGameModel(commController);
			this.model.connectToTable(commController);
			this.mainView.setUserName(model.getUserName());
			this.mainView.setBalance(model.getBalance());
			
			// ha bármilyen bug esetén a QUIT gomb letiltva marad, akkor az asztalt nem lehet elhagyni...
			mainGamePane.setOnKeyPressed(key -> {
				if (key.getCode() == KeyCode.Q) {
					frameController.setTableListerFXML();
				}
			});
		} catch (RemoteException e) {
			remoteExceptionHandler();
		}
		setButtonsDisability(true);
		modifyQuitButtonDisability(false);
		setTextArea();
		Platform.runLater(() -> textArea.appendText("Felhasználónevem: " + model.getUserName()));
	}

	@Override
	public void update(Object updateMsg) {
		// ha a ház küld utasítást
		int whosOn = -1;
		if (updateMsg instanceof HoldemHouseCommand) {
			HoldemHouseCommand houseHoldemCommand = (HoldemHouseCommand)updateMsg;
			whosOn = houseHoldemCommand.getWhosOn();
			logHouseCommand(houseHoldemCommand);
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
		} else if (updateMsg instanceof HoldemPlayerCommand) {
			HoldemPlayerCommand holdemPlayerCommand = (HoldemPlayerCommand)updateMsg;
			whosOn = holdemPlayerCommand.getWhosOn();
			logPlayerCommand(holdemPlayerCommand);
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
	 * A szerver FLOP típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private synchronized void receivedFlopHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				synchronized (textArea) {
					textArea.appendText(Arrays.asList(houseHoldemCommand.getCards()).stream().map(Object::toString).collect(Collectors.joining(" ")));
				}
			}
		});
		((HoldemMainGameView)mainView).receivedFlopHouseCommand(houseHoldemCommand);
	}

	/**
	 * A szerver TURN típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private void receivedTurnHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		synchronized (textArea) {
			textArea.appendText(Arrays.asList(houseHoldemCommand.getCards()).stream().map(Object::toString).collect(Collectors.joining(" ")));
		}
		((HoldemMainGameView)mainView).receivedTurnHouseCommand(houseHoldemCommand);
	}

	/**
	 * A szerver RIVER típusú utasítás küldött.
	 * @param houseHoldemCommand az utasítás
	 */
	private void receivedRiverHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		synchronized (textArea) {
			textArea.appendText(Arrays.asList(houseHoldemCommand.getCards()).stream().map(Object::toString).collect(Collectors.joining(" ")));
		}
		((HoldemMainGameView)mainView).receivedRiverHouseCommand(houseHoldemCommand);
	}
}