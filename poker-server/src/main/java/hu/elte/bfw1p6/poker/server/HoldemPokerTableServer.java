package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

/**
 * Póker játékasztal-szerver holdem játékhoz.
 * @author feher
 *
 */
public class HoldemPokerTableServer extends AbstractPokerTableServer {

	private static final long serialVersionUID = 2753404861902526567L;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben).
	 */
	private HoldemHouseCommandType actualHoldemHouseCommandType;

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		super(pokerTable);
		this.houseCards = new ArrayList<>();
		prepareNewRound();
	}
	
	@Override
	protected void prepareNewRound() {
		actualHoldemHouseCommandType = HoldemHouseCommandType.values()[0];
		houseCards.clear();
	}

	@Override
	protected void nextStep() {
		actualHoldemHouseCommandType = HoldemHouseCommandType.values()[(actualHoldemHouseCommandType.ordinal() + 1) % HoldemHouseCommandType.values().length];
	}

	@Override
	protected HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		return new HoldemHouseCommand().setUpBlindCommand(fixSitPosition, nthPlayer, clients.size(), dealer, whosOn, clientsNames);
	}
	
	@Override
	protected HouseCommand houseDealCommandFactory(Card[] cards) {
		return new HoldemHouseCommand().setUpDealCommand(cards, whosOn);
	}

	@Override
	protected PlayerCommand playerFoldCommandFactory(int whosOn) {
		return new HoldemPlayerCommand().setUpFoldCommand(whosOn);
	}
	
	@Override
	protected PlayerCommand playerQuitCommandFactory(String sender) {
		return new HoldemPlayerCommand().setUpQuitCommand(sender, whosOn);
	}

	@Override
	protected HouseCommand houseWinnerCommandFactory(Card[] cards, int winner, int whosOn) {
		return new HoldemHouseCommand().setUpWinnerCommand(cards, winner, whosOn);
	}

	@Override
	protected synchronized void receivedPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// ha valid klienstől érkezik üzenet, azt feldolgozzuk, körbeküldjük
		if (clients.contains(client)) {
			HoldemPlayerCommand holdemPlayerCommand = (HoldemPlayerCommand)playerCommand;
			switch(holdemPlayerCommand.getPlayerCommandType()) {
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
				receivedFoldPlayerCommand();
				break;
			}
			case RAISE: {
				receivedRaisePlayerCommand(holdemPlayerCommand);
				break;
			}
			case QUIT: {
				receivedQuitPlayerCommand(client, holdemPlayerCommand);
				break;
			}
			default:
				throw new IllegalArgumentException();
			}
			startAutomateQuitTask(holdemPlayerCommand);
		} else if (waitingClients.contains(client)) {
			if (playerCommand.getCommandType() == "QUIT") {
				receivedQuitPlayerCommandFromWaitingPlayer(client);
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * CHECK típusú utasítás érkezett egy klienstől.
	 */
	@Override
	protected void receivedCheckPlayerCommand(PlayerCommand playerComand) {
		++votedPlayers;
		if (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND) {
			playerComand.setWinnerCommand(true);
		}
	}

	@Override
	protected void nextRound() {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
		if (playersInRound <= 1 || (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND && votedPlayers >= playersInRound)) {
			bookMoneyStack(houseCards);
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				// flopnál, turnnél, rivernél, winnernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1) % leftRoundMask.length;
				whosOn = findNextValidClient(whosOn);
				HoldemHouseCommand houseHoldemCommand = new HoldemHouseCommand();
				switch (actualHoldemHouseCommandType) {
				case FLOP: {
					Card[] cards = new Card[]{deck.popCard(), deck.popCard(), deck.popCard()};
					Collections.addAll(houseCards, cards);
					houseHoldemCommand.setUpFlopTurnRiverCommand(HoldemHouseCommandType.FLOP, cards, whosOn);
					break;
				}
				case TURN: {
					Card[] cards = new Card[]{deck.popCard()};
					houseCards.add(cards[0]);
					houseHoldemCommand.setUpFlopTurnRiverCommand(actualHoldemHouseCommandType, cards, whosOn);
					break;
				}
				case RIVER: {
					Card[] cards = new Card[]{deck.popCard()};
					houseCards.add(cards[0]);
					houseHoldemCommand.setUpFlopTurnRiverCommand(actualHoldemHouseCommandType, cards, whosOn);
					break;
				}
				case WINNER: {
					houseHoldemCommand = (HoldemHouseCommand)winner();
					break;
				}
				default:
					throw new IllegalArgumentException();
				}
				notifyClients(houseHoldemCommand);
				nextStep();
				votedPlayers = 0;
			}
		}
	}

	@Override
	public synchronized void join(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			pingWaitingClients();
			if (clients.size() + waitingClients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException(ERR_TABLE_FULL);
			}
			if (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND) {
				preJoin(client, userName);
				startRound();
			} else {
				waitingJoin(client, userName);
			}
		}
	}
}