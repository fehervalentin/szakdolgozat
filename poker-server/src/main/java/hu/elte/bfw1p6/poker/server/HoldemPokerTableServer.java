package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.server.logic.HoldemHandEvaluator;

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

	/**
	 * Ház lapjai.
	 */
	private List<Card> houseCards;

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		super(pokerTable);
		this.houseCards = new ArrayList<>();
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
				break;
			}
			endOfReceivedPlayerCommand(holdemPlayerCommand);
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
	protected void nextRound() throws RemoteException {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
		System.out.println("VotedPlayers: " + votedPlayers);
		System.out.println("Players in round: " + playersInRound);
		if (playersInRound <= 1 || (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND && votedPlayers >= playersInRound)) {
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				// flopnál, turnnél, rivernél, winnernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1) % foldMask.length;
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
					winner(houseHoldemCommand);
				}
				default:
					break;
				}
				System.out.println("Next round");
				notifyClients(houseHoldemCommand);
				nextStep();
				votedPlayers = 0;
			}
		}
	}

	@Override
	protected void winner(HouseCommand houseCommand) {
		HoldemHouseCommand holdemHouseCommand = (HoldemHouseCommand)houseCommand;
		List<IPlayer> winnerList = HoldemHandEvaluator.getInstance().getWinner(houseCards, players);
		Card[] cards = winnerList.get(0).getCards();
		// TODO: és mi van ha döntetlen? Nem kezelem le......
		//TODO: aki nyert, annak el kell számolni a moneystacket
		int winner = -1;
		System.out.println("Players size: " + players.size());
		for (int i = 0; i < players.size(); i++) {
			boolean gotIt = true;
			for (int j = 0; j < cards.length; j++) {
				if (!players.get(i).getCards()[j].equals(cards[j])) {
					gotIt = false;
					break;
				}
			}
			if (gotIt) {
				winner = i;
				break;
			}
		}
		long count = IntStream.range(0, foldMask.length).filter(i -> foldMask[i]).count();
		long count2 = IntStream.range(0, quitMask.length).filter(i -> foldMask[i]).count();
		winner += (count + count2);
		winner %= foldMask.length;
		System.out.println("Hányan dobták a lapjaikat: " + count);
		System.out.println("A győztes sorszáma: " + winner);
		System.out.println("A győztes kártyalapjai: " + Arrays.toString(cards));
		holdemHouseCommand.setUpWinnerCommand(cards, winner, whosOn);
	}
}