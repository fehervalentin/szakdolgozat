package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.server.logic.Deck;

/**
 * Maga a póker asztal megvalósítása
 * @author feher
 *
 */
public class HoldemPokerTableServer extends UnicastRemoteObject {

	private static final long serialVersionUID = 2753404861902526567L;

	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";
	private final String ERR_TABLE_FULL = "Az asztal betelt, nem tudsz csatlakozni!";

	/**
	 * Maga az asztal entitás.
	 */
	private PokerTable pokerTable;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben).
	 */
	private HoldemHouseCommandType actualHoldemHouseCommandType;

	/**
	 * Maga a pénz stack.
	 */
	private BigDecimal moneyStack;

	/**
	 * Kliensek (observerek).
	 */
	private List<RemoteObserver> clients;

	/**
	 * Kártyapakli.
	 */
	private Deck deck;

	/**
	 * Ház lapjai.
	 */
	private List<Card> houseCards;

	/**
	 * Maguk a játékosok.
	 */
	private List<PokerPlayer> players;
	
	/**
	 * A kliensek username-jei (mert a PokerPlayerben a userName-re nincs setter! (perzisztálást védi...)
	 */
	private List<String> clientsNames;

	/**
	 * Hány játékos játszik az adott körben.
	 */
	private int playersInRound;

	/**
	 * Ki az osztó az adott körben.
	 */
	private int dealer = -1;

	/**
	 * Ki van soron éppen.
	 */
	private int whosOn;

	/**
	 * Hány játékos adott már le voksot az adott körben (raise-nél = 1).
	 */
	private int votedPlayers;

	/**
	 * Legalább hány játékos kell, hogy elinduljon a játék.
	 * Asztaltól fogom lekérni.
	 */
	@Deprecated
	private int minPlayer = 3;

	private int foldCounter;
	
	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		this.houseCards = new ArrayList<>();
//		this.playersCards = new HashMap<>();
		this.clients = new ArrayList<>();
		this.moneyStack = BigDecimal.ZERO;
		this.players = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
	}

	/**
	 * Az asztalhoz való csatlakozás.
	 * @param client a csatlakozni kívánó kliens
	 * @param userName a csatlakozni kívánó játékos neve
	 * @throws PokerTooMuchPlayerException
	 */
	public synchronized void join(RemoteObserver client, String userName) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException(ERR_TABLE_FULL);
			} else {
				clients.add(client);
				clientsNames.add(userName);
				System.out.println("JOIN: " + client.toString());
				startRound();
			}
		}
	}

	private void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
			foldCounter = 0;
			//			a vakokat kérem be legelőször
			//			actualHoldemHouseCommandType = HoldemHouseCommandType.BLIND;
			actualHoldemHouseCommandType = HoldemHouseCommandType.values()[0];
			// megnézem, hogy aktuális hány játékos van az asztalnál
			playersInRound = clients.size();
			//következő játékos a dealer
			++dealer;
			//nem baj, ha körbeértünk...
			dealer %= playersInRound;
			//a kártyapaklit megkeverjük
			deck.reset();
			//senki sem beszélt még
			votedPlayers = 0;
			//a dealertől balra ülő harmadik játékos kezd
			whosOn = (dealer + 3) % playersInRound;
			//törlöm a ház lapjait
			houseCards.clear();
			//törlöm a játékosokat
			players.clear();
			//törlöm a játékosok lapjait
//			playersCards.clear();
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers();
		}
	}

	private void collectBlinds() {
		for (int i = 0; i < clients.size(); i++) {
			HoldemHouseCommand houseHoldemCommand = new HoldemHouseCommand();
			houseHoldemCommand.setUpBlindCommand(i, clients.size(), dealer, whosOn, clientsNames);
			sendPokerCommand(i, houseHoldemCommand);
		}
		nextStep();
	}

	private void sendPokerCommand(int i, PokerCommand pokerCommand) {
		System.out.println("spc, ertesitem a " + i + ". klienst!");
		if (pokerCommand instanceof HoldemHouseCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemHouseCommand)pokerCommand).getHouseCommandType());
		} else if (pokerCommand instanceof HoldemPlayerCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemPlayerCommand)pokerCommand).getPlayerCommandType());
		}
		System.out.println("-----------------------------------------------");
		new Thread() {

			@Override
			public void run() {
				try {
					clients.get(i).update(pokerCommand);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}.start();
	}

	private void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			Card[] cards = new Card[]{deck.popCard(), deck.popCard()};
			PokerPlayer pokerPlayer = new PokerPlayer();
			pokerPlayer.setCards(cards);
			players.add(pokerPlayer);
			HoldemHouseCommand pokerCommand = new HoldemHouseCommand();
			pokerCommand.setUpDealCommand(cards, whosOn);
			sendPokerCommand(i, pokerCommand);
		}
		nextStep();
	}

	private void notifyClients(PokerCommand pokerCommand) {
		int i = 0;
		for (RemoteObserver pokerTableServerObserver : clients) {
			System.out.println("nc, ertesitem a " + i + ". klienst!");
			if (pokerCommand instanceof HoldemHouseCommand) {
				System.out.println("Utasitas típusa: " + ((HoldemHouseCommand)pokerCommand).getHouseCommandType());
			} else if (pokerCommand instanceof HoldemPlayerCommand) {
				System.out.println("Utasitas típusa: " + ((HoldemPlayerCommand)pokerCommand).getPlayerCommandType());
			}
			System.out.println("-----------------------------------------------");
			new Thread() {

				@Override
				public void run() {
					try {
						pokerTableServerObserver.update(pokerCommand);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}}.start();
				++i;
		}
	}

	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	public synchronized void receivePlayerCommand(RemoteObserver client, HoldemPlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// ha valid klienstől érkezik üzenet, azt feldolgozzuk, körbeküldjük
		if (clients.contains(client)) {
			switch(playerCommand.getPlayerCommandType()) {
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

	private void nextRound() throws RemoteException {
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
					houseHoldemCommand.setUpFlopTurnRiverCommand(HoldemHouseCommandType.BLIND, cards, whosOn, foldCounter);
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

	private void winner(HoldemHouseCommand houseHoldemCommand) {
		//TODO: aki foldolt annak ne vegyük figyelembe a lapjait
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
		houseHoldemCommand.setUpWinnerCommand(cards, winner_);
	}
	
	private void nextStep() {
		actualHoldemHouseCommandType = HoldemHouseCommandType.values()[(actualHoldemHouseCommandType.ordinal() + 1) % HoldemHouseCommandType.values().length];
	}

	private void refreshBalance(HoldemPlayerCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	private boolean isThereEnoughMoney(User u, HoldemPlayerCommand playerCommand) throws PokerUserBalanceException {
		BigDecimal newBalance = u.getBalance().subtract(playerCommand.getCallAmount());
		moneyStack = moneyStack.add(playerCommand.getCallAmount());
		if (playerCommand.getRaiseAmount() != null) {
			moneyStack = moneyStack.add(playerCommand.getRaiseAmount());
			newBalance = newBalance.subtract(playerCommand.getRaiseAmount());
		}
		if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new PokerUserBalanceException(ERR_BALANCE_MSG);
		}
		return true;
	}
}