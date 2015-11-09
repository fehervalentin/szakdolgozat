package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.server.logic.Deck;

public abstract class AbstractPokerTableServer extends UnicastRemoteObject {

	private static final long serialVersionUID = -2646114665508361840L;

	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";
	private final String ERR_TABLE_FULL = "Az asztal betelt, nem tudsz csatlakozni!";

	/**
	 * Maga az asztal entitás.
	 */
	protected PokerTable pokerTable;
	
	/**
	 * Maga a pénz stack.
	 */
	protected BigDecimal moneyStack;

	/**
	 * Kliensek (observerek).
	 */
	protected List<PokerRemoteObserver> clients;

	/**
	 * Kártyapakli.
	 */
	protected Deck deck;

	/**
	 * Maguk a játékosok.
	 */
	protected List<PokerPlayer> players;

	/**
	 * A kliensek username-jei (mert a PokerPlayerben a userName-re nincs setter! (perzisztálást védi...)
	 */
	protected List<String> clientsNames;

	/**
	 * Hány játékos játszik az adott körben.
	 */
	protected int playersInRound;

	/**
	 * Ki az osztó az adott körben.
	 */
	protected int dealer = -1;

	/**
	 * Ki van soron éppen.
	 */
	protected int whosOn;
	
	protected int cardsToHand;

	/**
	 * Hány játékos adott már le voksot az adott körben (raise-nél = 1).
	 */
	protected int votedPlayers;

	/**
	 * Legalább hány játékos kell, hogy elinduljon a játék.
	 * Asztaltól fogom lekérni.
	 */
	@Deprecated
	protected int minPlayer = 2;

	/**
	 * A játékosok száma, akik eldobták a lapjaikat.
	 */
	protected int foldCounter;

	protected AbstractPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super();
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		this.clients = new ArrayList<>();
		this.moneyStack = BigDecimal.ZERO;
		this.players = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
		this.cardsToHand = pokerTable.getPokerType().getCardsToPlayers();
	}


	/**
	 * Asztalhoz való csatlakozás.
	 * @param client a csatlakozni kívánó kliens
	 * @param userName a csatlakozni kívánó játékos neve
	 * @throws PokerTooMuchPlayerException
	 */
	public synchronized void join(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException(ERR_TABLE_FULL);
			} else {
				clients.add(client);
				clientsNames.add(userName);
				System.out.println("JOIN: " + client.toString());
				//ha elegen vagyunk az asztalnál, akkor indulhat a játék
				if (clients.size() >= minPlayer) {
					startRound();
				}
			}
		}
	}

	/**
	 * Inicializáció metódus új kör kezdés esetére.
	 */
	protected void preStartRound() {
		//még senki sem dobta el a lapjait
		foldCounter = 0;
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
		//törlöm a játékosokat
		players.clear();
	}

	/**
	 * Értesíti az i. klienst
	 * @param i a kliens sorszáma
	 * @param pokerCommand az utsítás
	 */
	protected void notifyNthClient(int i, PokerCommand pokerCommand) {
		System.out.println("Ertesitem a " + i + ". klienst!");
		System.out.println("Utasitas típusa: " + pokerCommand.getCommandType());
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

	/**
	 * Az összes klienst értesíti.
	 * @param pokerCommand az utasítás
	 */
	protected void notifyClients(PokerCommand pokerCommand) {
		for (int i = 0; i < clients.size(); i++) {
			notifyNthClient(i, pokerCommand);
		}
	}

	/**
	 * Lecsatlakozás a játéktábla szerverről.
	 * @param client lecsatlakozandó kliens
	 */
	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	/**
	 * Frissíti a kliens egyenlegét.
	 * @param playerCommand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void refreshBalance(PlayerCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	/**
	 * Levizsgálja, hogy van-e elegendő zsetonja a felhasználónak.
	 * @param u a felhasználó
	 * @param playerCommand az utasítás
	 * @return ha van elegendő zsetonja, akkor true, különben false.
	 * @throws PokerUserBalanceException
	 */
	protected boolean isThereEnoughMoney(User u, PlayerCommand playerCommand) throws PokerUserBalanceException {
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

	/**
	 * Blind utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedBlindPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		--whosOn;
	}

	/**
	 * Call utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedCallPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		++votedPlayers;
	}

	/**
	 * Check utasítás érkezett egy klienstől.
	 */
	protected void receivedCheckPlayerCommand() {
		++votedPlayers;
	}

	/**
	 * Fold utasítás érkezett egy klienstől.
	 */
	protected void receivedFoldPlayerCommand() {
		//++votedPlayers;
		--playersInRound;
		//az ő lapjait már ne vegyük figyelembe winnerkor
		players.remove(whosOn);
		--whosOn;
		++foldCounter;
		// mert aki nagyobb az ő sorszámánál, az lejjebb csúszik eggyel.
	}
	
	/**
	 * Raise utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedRaisePlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		votedPlayers = 1;
	}
	
	protected void receivedQuitPlayerCommand(PokerRemoteObserver client, PlayerCommand playerComand) {
		System.out.println("WhosQuit param: " + playerComand.getWhosQuit());
		System.out.println("Kliens visszakeresve: " + clients.indexOf(client));
		clients.remove(client);
		//				++votedPlayers;
		--playersInRound;
		--whosOn;
	}
	
	protected void endOfReceivedPlayerCommand(PlayerCommand playerComand) throws RemoteException {
		++whosOn;
		whosOn %= playersInRound;
		playerComand.setWhosOn(whosOn);
		notifyClients(playerComand);

		nextRound();
	}

	protected void startRound() {
		preStartRound();
		prepareNewRound();
		collectBlinds();
		dealCardsToPlayers();
	}
	
	protected void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			Card[] cards = new Card[cardsToHand];
			for (int j = 0; j < cardsToHand; j++) {
				cards[j] = deck.popCard();
			}
			PokerPlayer pokerPlayer = new PokerPlayer(clientsNames.get(i));
			pokerPlayer.setCards(cards);
			players.add(pokerPlayer);
			notifyNthClient(i, houseDealCommandFactory(cards));
		}
		nextStep();
	}

	protected void collectBlinds() {
		for (int i = 0; i < clients.size(); i++) {
			notifyNthClient(i, houseBlindCommandFactory(i, i, clients.size(), dealer, whosOn, clientsNames));
		}
		nextStep();
	}
	
	protected abstract HouseCommand houseDealCommandFactory(Card[] cards);
	
	protected abstract HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames);

	protected abstract void nextStep();

	protected abstract void winner(HouseCommand houseCommand);

	protected abstract void nextRound() throws RemoteException;

	protected abstract void receivedPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	protected abstract void prepareNewRound();
}
