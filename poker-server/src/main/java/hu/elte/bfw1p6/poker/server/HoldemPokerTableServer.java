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
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

/**
 * Maga a póker asztal megvalósítása
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

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
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
	protected HouseCommand houseBlindCommandFactory(int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		HoldemHouseCommand holdemHouseCommand = new HoldemHouseCommand();
		holdemHouseCommand.setUpBlindCommand(nthPlayer, clients.size(), dealer, whosOn, clientsNames);
		return holdemHouseCommand;
	}
	
	@Override
	protected HouseCommand houseDealCommandFactory(Card[] cards) {
		HoldemHouseCommand holdemHouseCommand = new HoldemHouseCommand();
		holdemHouseCommand.setUpDealCommand(cards, whosOn);
		return holdemHouseCommand;
	}

	@Override
	protected synchronized void receivePlayerCommand(RemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
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
				receivedFoldPlayerCommand(holdemPlayerCommand);
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
			endOfReceivePlayerCommand(holdemPlayerCommand);
		}
	}

	@Override
	protected void nextRound() throws RemoteException {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
		System.out.println("VotedPlayers: " + votedPlayers);
		System.out.println("Players in round: " + playersInRound);
		if (playersInRound == 1 || (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND && votedPlayers >= playersInRound)) {
			//TODO: itt is kell értékelni, hogy ki nyert
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				HoldemHouseCommand houseHoldemCommand = new HoldemHouseCommand();
				// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1 + foldCounter) % playersInRound;
				switch (actualHoldemHouseCommandType) {
				case FLOP: {
					Card[] cards = new Card[]{deck.popCard(), deck.popCard(), deck.popCard()};
					houseCards.add(cards[0]);
					houseCards.add(cards[1]);
					houseCards.add(cards[2]);
					houseHoldemCommand.setUpFlopTurnRiverCommand(HoldemHouseCommandType.FLOP, cards, whosOn, foldCounter);
					break;
				}
				case TURN: {
					Card[] cards = new Card[]{deck.popCard()};
					houseCards.add(cards[0]);
					houseHoldemCommand.setUpFlopTurnRiverCommand(actualHoldemHouseCommandType, cards, whosOn, foldCounter);
					break;
				}
				case RIVER: {
					Card[] cards = new Card[]{deck.popCard()};
					houseCards.add(cards[0]);
					houseHoldemCommand.setUpFlopTurnRiverCommand(actualHoldemHouseCommandType, cards, whosOn, foldCounter);
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
		//TODO: aki foldolt annak ne vegyük figyelembe a lapjait lehet hogy már kész...
		List<IPlayer> winner = HoldemHandEvaluator.getInstance().getWinner(houseCards, players);
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