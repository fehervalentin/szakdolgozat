package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.HousePokerCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHousePokerCommandType;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerPokerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

/**
 * Maga a póker asztal megvalósítása.
 * @author feher
 *
 */
public class HoldemPokerTableServer extends AbstractPokerTableServer<HoldemHousePokerCommandType, HoldemPlayerPokerCommandType> {

	private static final long serialVersionUID = 2737496961750222946L;
	
	/**
	 * Aktuális utasítás típusa.
	 */
	private HoldemHousePokerCommandType actualHouseCommandType;
	
	/**
	 * Ház lapjai.
	 */
	private List<Card> houseCards;

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
		this.houseCards = new ArrayList<>();
	}

	@Override
	protected void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
			prepareNextRound();
			//törlöm a ház lapjait
			houseCards.clear();
			//a vakokat kérem be legelőször
			actualHouseCommandType = HoldemHousePokerCommandType.values()[0];
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers(2);
		}
	}

	@Override
	public synchronized void receivePlayerCommand(RemoteObserver client, PlayerCommand<HoldemPlayerPokerCommandType> playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// ha valid klienstől érkezik üzenet, azt feldolgozzuk, körbeküldjük
		if (clients.contains(client)) {
			switch(playerCommand.getPlayerCommandType().getActual()) {
			case BLIND: {
				refreshBalance(playerCommand);
				--whosOn;
				break;
			}
			case CALL: {
				refreshBalance(playerCommand);
				++votedPlayers;
				break;
			}
			case CHECK: {
				++votedPlayers;
				break;
			}
			case FOLD: {
				//				++votedPlayers;
				--playersInRound;
				players.remove(whosOn);
				--whosOn;
				++foldCounter;
				// mert aki nagyobb az ő sorszámánál, az lejjebb csúszik eggyel.
				break;
			}
			case RAISE: {
				refreshBalance(playerCommand);
				votedPlayers = 1;
				break;
			}
			case QUIT: {
				System.out.println("WhosQuit param: " + playerCommand.getWhosQuit());
				System.out.println("Kliens visszakeresve: " + clients.indexOf(client));
				clients.remove(client);
				//				++votedPlayers;
				--playersInRound;
				--whosOn;
				break;
			}
			default:
				break;
			}
			++whosOn;
			whosOn %= playersInRound;
			playerCommand.setWhosOn(whosOn);
			notifyClients(playerCommand);

			nextRound();
		}
	}

	@Override
	protected void nextRound() throws RemoteException {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
		System.out.println("VotedPlayers: " + votedPlayers);
		System.out.println("Players in round: " + playersInRound);
		if (playersInRound == 1 || (actualHouseCommandType == HoldemHousePokerCommandType.BLIND && votedPlayers >= playersInRound)) {
			//TODO: itt is kell értékelni, hogy ki nyert
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				HoldemHouseCommand houseHoldemCommand = new HoldemHouseCommand();
				// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1 + foldCounter) % playersInRound;
				switch (actualHouseCommandType.getActual()) {
				case FLOP: {
					houseCards.add(deck.popCard());
					houseCards.add(deck.popCard());
					houseCards.add(deck.popCard());
					Card[] cards = new Card[]{houseCards.get(0), houseCards.get(1), houseCards.get(2)};
					houseHoldemCommand.setUpFlopCommand(cards, whosOn, foldCounter);
					break;
				}
				case TURN: {
					houseCards.add(deck.popCard());
					houseHoldemCommand.setUpTurnCommand(new Card[]{houseCards.get(3)}, whosOn, foldCounter);
					break;
				}
				case RIVER: {
					houseCards.add(deck.popCard());
					houseHoldemCommand.setUpRiverCommand(new Card[]{houseCards.get(4)}, whosOn, foldCounter);
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
	protected void winner(HousePokerCommand houseHoldemCommand) {
		List<IPlayer> winner = HoldemHandEvaluator.getInstance().getWinner(houseCards, players);
		Card[] cards = winner.get(0).getCards();
		// TODO: és mi van ha döntetlen?
		int winner_ = -1;
		System.out.println("Players size: " + players.size());
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getCards()[0].equals(cards[0]) && players.get(i).getCards()[1].equals(cards[1])) {
//				winner_ = (i - foldCounter) % playersInRound;
				winner_ = i;
				break;
			}
		}
		System.out.println("A győztes sorszáma: " + winner_);
		System.out.println("A győztes első lapja: " + cards[0]);
		System.out.println("A győztes második lapja: " + cards[1]);
		HoldemHouseCommand asd = (HoldemHouseCommand)houseHoldemCommand;
		
		asd.setUpWinnerCommand(cards, winner_);
	}

	@Override
	protected HousePokerCommand<HoldemHousePokerCommandType> getNewCommand() {
		return new HoldemHouseCommand();
	}

	@Override
	protected void nextStep() {
		actualHouseCommandType = actualHouseCommandType.getNext();
	}
}