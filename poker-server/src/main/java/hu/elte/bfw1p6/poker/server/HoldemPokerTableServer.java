package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.Card;
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
	/**
	 * 
	 */
	private static final long serialVersionUID = 2753404861902526567L;

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;

	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";

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
	 * Hát lapjai.
	 */
	private List<Card> houseCards;
	
	/**
	 * Kliensek lapjai.
	 */
	private HashMap<Integer, List<Card>> playersCards;

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

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		this.houseCards = new ArrayList<>();
		this.playersCards = new HashMap<>();
		this.clients = new ArrayList<>();
		this.moneyStack = new BigDecimal(0);
	}

	/**
	 * Az asztalhoz való csatlakozás.
	 * @param client a csatlakozni kívánó kliens
	 * @throws PokerTooMuchPlayerException
	 */
	public synchronized void join(RemoteObserver client) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException("Az asztal betelt, nem tudsz csatlakozni!");
			} else {
				clients.add(client);
				startRound();
			}
		}
	}

	private void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
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
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers();
		}
	}

	private void collectBlinds() {
		for (int i = 0; i < clients.size(); i++) {
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, i, clients.size(), dealer, whosOn);
			sendPokerCommand(i, pokerCommand);
		}
		nextStep();
	}

	private void sendPokerCommand(int i, PokerCommand pokerCommand) {
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
			Card c1 = deck.popCard();
			Card c2 = deck.popCard();
			playersCards.put(i, new ArrayList<>());
			playersCards.get(i).add(c1);
			playersCards.get(i).add(c2);
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard(), whosOn);
			sendPokerCommand(i, pokerCommand);
		}
		nextStep();
	}

	private void notifyClients(PokerCommand pokerCommand) {
		for (RemoteObserver pokerTableServerObserver : clients) {
			System.out.println("ertesitem a " + pokerTableServerObserver.toString() + " klienst!");
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
		}
	}

	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	public synchronized void receivePlayerCommand(RemoteObserver client, PlayerHoldemCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException {
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
				--whosOn;
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
			System.out.println("WhosOn: " + whosOn);
			playerCommand.setWhosOn(whosOn);
			notifyClients(playerCommand);
			// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
			if (playersInRound == 1 || (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND && votedPlayers >= playersInRound)) {
				// itt kell eldönteni, hogy ki nyert, és azt körbe is kell ám küldeni!
				System.out.println("új kör");
				startRound();
			} else {
				// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
				if (votedPlayers >= playersInRound) {
					PokerCommand pokerCommand = null;
					// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást!
					whosOn = (dealer + 1) % playersInRound;
					switch (actualHoldemHouseCommandType) {
					case FLOP: {
						for (int i = 0; i < 3; i++) {
							houseCards.add(deck.popCard());
						}
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, houseCards.get(0), houseCards.get(1), houseCards.get(2), whosOn);
						break;
					}
					case TURN: {
						houseCards.add(deck.popCard());
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, houseCards.get(3), whosOn);
						break;
					}
					case RIVER: {
						houseCards.add(deck.popCard());
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, houseCards.get(4), whosOn);
						break;
					}
					default:
						break;
					}
					notifyClients(pokerCommand);
					nextStep();
					votedPlayers = 0;
				}
			}
		}
	}
	
	private void nextStep() {
		actualHoldemHouseCommandType = HoldemHouseCommandType.values()[(actualHoldemHouseCommandType.ordinal() + 1) % HoldemHouseCommandType.values().length];
	}

	private void refreshBalance(PlayerHoldemCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	private boolean isThereEnoughMoney(User u, PlayerHoldemCommand playerCommand) throws PokerUserBalanceException {
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