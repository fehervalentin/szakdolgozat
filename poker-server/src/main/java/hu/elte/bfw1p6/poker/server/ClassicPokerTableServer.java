package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

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
	protected void prepareNewRound() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[0];
	}

	@Override
	protected void nextStep() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[(actualClassicHouseCommandType.ordinal() + 1) % ClassicHouseCommandType.values().length];
	}

	@Override
	protected HouseCommand houseDealCommandFactory(Card[] cards) {
		ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
		classicHouseCommand.setUpDealCommand(cards, whosOn);
		return classicHouseCommand;
	}

	@Override
	protected HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
		classicHouseCommand.setUpBlindCommand(fixSitPosition, nthPlayer, clients.size(), dealer, whosOn, clientsNames);
		return classicHouseCommand;
	}

	@Override
	protected void nextRound() throws RemoteException {
//		System.out.println("VotedPlayers: " + votedPlayers);
//		System.out.println("Players in round: " + playersInRound);
		if (playersInRound == 1 || (actualClassicHouseCommandType == ClassicHouseCommandType.values()[0] && votedPlayers >= playersInRound)) {
			//TODO: itt is kell értékelni, hogy ki nyert
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
				// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1 + foldCounter) % playersInRound;
				switch (actualClassicHouseCommandType) {
				case CHANGE: {
					classicHouseCommand.setUpChangeCommand(whosOn);
					break;
				}
				case DEAL2: {
					for (int i = 0; i < clients.size(); i++) {
						ClassicHouseCommand chc = new ClassicHouseCommand();
						chc.setUpDeal2Command(players.get(i).getCards(), whosOn);
						notifyNthClient(i, chc);
					}
					break;
				}
				case WINNER: {
					winner(classicHouseCommand);
					break;
				}
				default:
					throw new IllegalArgumentException();
				}
				System.out.println("Next round");
				if (actualClassicHouseCommandType != ClassicHouseCommandType.DEAL2) {
					notifyClients(classicHouseCommand);
				}
				nextStep();
				votedPlayers = 0;
			}
		}
	}

	@Override
	protected void receivedPlayerCommand(RemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
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
			case CHANGE: {
				receivedChangePlayerCommand(classicPlayerCommand);
			}
			default:
				break;
			}
			endOfReceivedPlayerCommand(classicPlayerCommand);
		}
	}

	private void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		List<Integer> markedCards = classicPlayerCommand.getMarkedCards();
		PokerPlayer pokerPlayer = null;
		for (PokerPlayer player : players) {
			if(player.getUserName().equals(classicPlayerCommand.getSender())) {
				pokerPlayer = player;
			}
		}
		for (Integer i : markedCards) {
			pokerPlayer.setNthCard(i, deck.popCard());
		}
		++votedPlayers;
	}

	@Override
	protected void winner(HouseCommand houseCommand) {
		ClassicHouseCommand holdemHouseCommand = (ClassicHouseCommand)houseCommand;
		//TODO: aki foldolt annak ne vegyük figyelembe a lapjait lehet hogy már kész...
		List<IPlayer> winner = HoldemHandEvaluator.getInstance().getWinner(new ArrayList<>(), players);
		Card[] cards = winner.get(0).getCards();
		// TODO: és mi van ha döntetlen?
		int winner_ = -1;
		System.out.println("Players size: " + players.size());
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getCards()[0].equals(cards[0]) && players.get(i).getCards()[1].equals(cards[1])) {
				//winner_ = (i - foldCounter) % playersInRound;
				winner_ = i;
				break;
			}
		}
		System.out.println("A győztes sorszáma: " + winner_);
		System.out.println("A győztes kártyalapjai: " + Arrays.toString(cards));
		holdemHouseCommand.setUpWinnerCommand(cards, winner_);
	}
}