package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ClassicPokerTableServer extends AbstractPokerTableServer {

	private static final long serialVersionUID = 3009724030721806069L;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben).
	 */
	private ClassicHouseCommandType actualClassicHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
	}


	@Override
	protected void startRound() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[0];
		collectBlinds();
		dealCardsToPlayers(5);

	}

	@Override
	protected void nextStep() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[(actualClassicHouseCommandType.ordinal() + 1) % ClassicHouseCommandType.values().length];
	}

	@Override
	protected void winner(HouseCommand houseCommand) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dealCardsToPlayers(int cardCount) {
		for (int i = 0; i < clients.size(); i++) {
			Card[] cards = new Card[cardCount];
			for (int j = 0; j < cardCount; j++) {
				cards[j] = deck.popCard();
			}
			PokerPlayer pokerPlayer = new PokerPlayer();
			pokerPlayer.setCards(cards);
			players.add(pokerPlayer);
			ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
			classicHouseCommand.setUpDealCommand(cards, whosOn);
			sendPokerCommand(i, classicHouseCommand);
		}
		nextStep();
	}

	@Override
	protected void collectBlinds() {
		for (int i = 0; i < clients.size(); i++) {
			ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
			classicHouseCommand.setUpBlindCommand(i, clients.size(), dealer, whosOn, clientsNames);
			sendPokerCommand(i, classicHouseCommand);
		}
		nextStep();
	}

	@Override
	protected void nextRound() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void receivePlayerCommand(RemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// ha valid klienstől érkezik üzenet, azt feldolgozzuk, körbeküldjük
		if (clients.contains(client)) {
			ClassicPlayerCommand classicPlayerCommand = (ClassicPlayerCommand)playerCommand;
			switch(classicPlayerCommand.getPlayerCommandType()) {
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
				receivedQuitPlayerCommand(client, classicPlayerCommand);
				break;
			}
			default:
				break;
			}
			endOfReceivePlayerCommand(classicPlayerCommand);
		}
	}

}
