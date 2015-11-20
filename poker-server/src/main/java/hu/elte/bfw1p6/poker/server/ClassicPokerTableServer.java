package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

/**
 * Póker játékasztal-szerver classic játékhoz.
 * @author feher
 *
 */
public class ClassicPokerTableServer extends AbstractPokerTableServer {

	private static final long serialVersionUID = 3009724030721806069L;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben).
	 */
	private ClassicHouseCommandType actualClassicHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		super(pokerTable);
		prepareNewRound();
	}

	@Override
	protected void prepareNewRound() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[0];
	}

	@Override
	protected void nextStep() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[(actualClassicHouseCommandType.ordinal() + 1) % ClassicHouseCommandType.values().length];
	}

	@Override
	protected HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		return new ClassicHouseCommand().setUpBlindCommand(fixSitPosition, nthPlayer, clients.size(), dealer, whosOn, clientsNames);
	}
	
	@Override
	protected HouseCommand houseDealCommandFactory(Card[] cards) {
		return new ClassicHouseCommand().setUpDealCommand(cards, whosOn);
	}

	@Override
	protected PlayerCommand playerQuitCommandFactory(String sender) {
		return new ClassicPlayerCommand().setUpQuitCommand(sender, whosOn);
	}
	
	@Override
	protected PlayerCommand playerFoldCommandFactory(int whosOn) {
		return new ClassicPlayerCommand().setUpFoldCommand(whosOn);
	}

	@Override
	protected HouseCommand houseWinnerCommandFactory(Card[] cards, int winner, int whosOn) {
		return new ClassicHouseCommand().setUpWinnerCommand(cards, winner, whosOn);
	}

	@Override
	protected void nextRound() {
		if (playersInRound <= 1 || (actualClassicHouseCommandType == ClassicHouseCommandType.values()[0] && votedPlayers >= playersInRound)) {
			if (playersInRound > 0) {
				bookMoneyStack(null);
			}
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
				// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de arról a szerver gondoskodik)
				whosOn = (dealer + 1) % leftRoundMask.length;
				whosOn = findNextValidClient(whosOn);
				switch (actualClassicHouseCommandType) {
				case CHANGE: {
					classicHouseCommand.setUpChangeCommand(whosOn);
					break;
				}
				case DEAL2: {
					IntStream.range(0, players.size()).forEach(i ->
						notifyNthClient(i, new ClassicHouseCommand().setUpDeal2Command(players.get(i).getCards(), whosOn))
					);
					break;
				}
				case WINNER: {
					classicHouseCommand = (ClassicHouseCommand)winner();
					break;
				}
				default:
					throw new IllegalArgumentException();
				}
				if (actualClassicHouseCommandType != ClassicHouseCommandType.DEAL2) {
					notifyClients(classicHouseCommand);
				}
				nextStep();
				votedPlayers = 0;
			}
		}
	}

	@Override
	protected void receivedPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
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
				receivedFoldPlayerCommand();
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
			case CHANGE: {
				receivedChangePlayerCommand(classicPlayerCommand);
				break;
			}
			default:
				throw new IllegalArgumentException();
			}
			startAutomateQuitTask(classicPlayerCommand);
		} else if (waitingClients.contains(client)) {
			if (playerCommand.getCommandType() == "QUIT") {
				receivedQuitPlayerCommandFromWaitingPlayer(client);
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	@Override
	protected void receivedCheckPlayerCommand(PlayerCommand playerComand) {
		++votedPlayers;
		if (actualClassicHouseCommandType == ClassicHouseCommandType.BLIND) {
			playerComand.setWinnerCommand(true);
		}
	}

	/**
	 * CHANGE típusú utasítás érkezett egy klienstől.
	 * @param classicPlayerCommand az utasítás
	 */
	private void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		PokerPlayer pokerPlayer = null;
		for (PokerPlayer player : players) {
			if(player.getUserName().equals(classicPlayerCommand.getSender())) {
				pokerPlayer = player;
			}
		}
		for (Integer i : classicPlayerCommand.getMarkedCards()) {
			pokerPlayer.setNthCard(i, deck.popCard());
		}
		++votedPlayers;
	}

	@Override
	public synchronized void join(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			pingWaitingClients();
			if (clients.size() + waitingClients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException(ERR_TABLE_FULL);
			}
			if (actualClassicHouseCommandType == ClassicHouseCommandType.BLIND) {
				preJoin(client, userName);
				startRound();
			} else {
				waitingJoin(client, userName);
			}
		}
	}
}