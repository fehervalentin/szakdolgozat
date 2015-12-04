package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.dao.UserDAO;
import hu.elte.bfw1p6.poker.server.logic.Deck;
import hu.elte.bfw1p6.poker.server.logic.HoldemHandEvaluator;

/**
 * A póker játékasztal-szerverek absztrakciója.
 * @author feher
 *
 */
public abstract class AbstractPokerTableServer extends UnicastRemoteObject {

	private static final long serialVersionUID = -2646114665508361840L;

	private final String ERR_BALANCE = "Nincs elég zsetonod!";
	protected final String ERR_TABLE_FULL = "Az asztal betelt, nem tudsz csatlakozni!";

	/**
	 * Szálak megfelelő végrehajtási sorrendjét biztosítja.
	 */
	private CountDownLatch latch;
	
	/**
	 * Az automatikus kiléptetési feladat időzítője.
	 */
	private Timer timer;

	/**
	 * Az automatikus kiléptető feladat.
	 */
	private TimerTask timerTask;

	/**
	 * Ház lapjai. Classic esetében null marad.
	 * (Azért kell az abstract osztályba, mert a hand evaluationnek nullként kell beadni.)
	 */
	protected List<Card> houseCards;

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
	 * A kliensek username-jei (mert a PokerPlayerben a userName-re nincs setter! (perzisztálást védi...)
	 */
	protected List<String> clientsNames;
	
	/**
	 * A kliensek, akik várakoznak a következő partyra (körre).
	 */
	protected List<PokerRemoteObserver> waitingClients;
	
	/**
	 * A kliensek nevei, akik várakoznak a következő partyra (körre).
	 */
	protected List<String> waitingClientsNames;

	/**
	 * Maguk a játékosok.
	 */
	protected List<User> users;

	/**
	 * Kártyapakli.
	 */
	protected Deck deck;

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

	/**
	 * Hány játékos adott már le voksot az adott körben (RAISE-nél = 1).
	 */
	protected int votedPlayers;

	/**
	 * Legalább hány játékos kell, hogy elinduljon a játék.
	 */
	protected int minPlayer = 2;

	/**
	 * A felhasználók adatainak módosítására szolgáló objektum.
	 */
	protected UserDAO userDAO;

	/**
	 * Kik azok a játékosok, akik FOLD vagy QUIT típusú utasítást küldtek.
	 */
	protected boolean[] leftRoundMask = new boolean[5];

	protected AbstractPokerTableServer(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		super();
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		
		this.clients = new ArrayList<>();
		this.users = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
		this.waitingClients = new ArrayList<>();
		this.waitingClientsNames = new ArrayList<>();
		
		this.moneyStack = BigDecimal.ZERO;
		this.userDAO = new UserDAO();
		this.timer = new Timer();
	}

	protected TimerTask createNewTimerTask() {
		return new TimerTask() {

			@Override
			public void run() {
				try {
					receivedPlayerCommand(clients.get(whosOn), playerQuitCommandFactory(clientsNames.get(whosOn)));
				} catch (PokerDataBaseException | PokerUserBalanceException e) {
					e.printStackTrace();
				}
			}
		};
	}

	protected int findNextValidClient(int whosOn) {
		whosOn %= leftRoundMask.length;
		int start = whosOn;
		while (leftRoundMask[whosOn]) {
			++whosOn;
			whosOn %= leftRoundMask.length;
			if (start == whosOn) break;
		}
		return whosOn;
	}

	/**
	 * Asztalhoz való csatlakozás.
	 * @param client a csatlakozni kívánó kliens
	 * @param userName a csatlakozni kívánó játékos neve
	 */
	public abstract void join(PokerRemoteObserver client, String userName);

	/**
	 * A várakozó klienseknek üzen, ha valamelyiknél megszakadt a kapcsolat, akkor kidobja.
	 */
	public void pingWaitingClients() {
		for (int i = waitingClients.size() - 1; i >= 0; i--) {
			try {
				waitingClients.get(i).update("ping");
			} catch (RemoteException e) {
				waitingClients.remove(i);
			}
		}
	}

	/**
	 * QUIT típusú utasítás érkezett egy klienstől.
	 * @param client a kilépendő kliens
	 * @param playerCommand az utasítás
	 */
	protected void receivedQuitPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) {
		int index = clients.indexOf(client);
		leftRoundMask[index] = true;
		--playersInRound;
		--whosOn;
		clientsNames.remove(index);
		clients.remove(index);
		if (clients.size() == 0) {
			timerTask = null;
		}
		//kell, mert a kliens így várja, hogy mit csináljon, szétküldöm a klienseknek, hogy
		//a szerver kit léptetett ki
		new Thread() {

			@Override
			public void run() {
				try {
					client.update(playerCommand);
				} catch (RemoteException e) {
					// ha épp az adott kliensnél (aki ki akart lépni) szakadt meg a kapcsolat
					// nem gond, hogy nem itt távolítom el, a timertask végrehajtása úgy is kidobja
				}
			}

		}.start();
		prepareNewRound();
	}

	/**
	 * Várakozó listán lévő kliens kilépett.
	 * @param client a kliens
	 */
	protected void receivedQuitPlayerCommandFromWaitingPlayer(PokerRemoteObserver client) {
		System.out.println("Waiting player left: " + client + " table: " + pokerTable.getName());
		waitingClients.remove(client);
	}

	/**
	 * FOLD típusú utasítás érkezett egy klienstől.
	 */
	protected void receivedFoldPlayerCommand() {
		leftRoundMask[whosOn] = true;
		--playersInRound;
	}

	/**
	 * Inicializáció metódus új kör kezdés esetére.
	 */
	protected void preStartRound() {
		//akik várakoznak, azokat felveszem teljes értékű játékosként
		clients.addAll(waitingClients);
		clientsNames.addAll(waitingClientsNames);
		waitingClients.clear();
		waitingClientsNames.clear();
		// megnézem, hogy aktuálisan hány játékos van az asztalnál
		playersInRound = clients.size();
		leftRoundMask = new boolean[playersInRound];
		//következő játékos a dealer
		++dealer;
		//a kártyapaklit megkeverjük
		deck.reset();
		//senki sem beszélt még
		votedPlayers = 0;
		//		if (playersInRound > 0) {
		//nem baj, ha körbeértünk...
		dealer %= leftRoundMask.length;
		//a dealertől balra ülő harmadik játékos kezd
		whosOn = (dealer + 3) % playersInRound;
		//		}
		//törlöm a játékosokat
		users.clear();
		//új játékosokat veszek fel
		for (int j = 0; j < clients.size(); j++) {
			try {
				users.add(userDAO.findByUserName(clientsNames.get(j)));
			} catch (PokerDataBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Az összes klienst értesíti.
	 * @param pokerCommand az utasítás
	 */
	protected void notifyClients(PokerCommand pokerCommand) {
		// meg kell várni, amíg az összes klienst értesítettük!
		latch = new CountDownLatch(clients.size());
		IntStream.range(0, clients.size()).forEach(i -> notifyNthClient(i, pokerCommand));
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Értesíti az i. klienst, ha RemoteException lép fel, akkor úgy vesszük,
	 * hogy QUIT típusú utasítást küldött.
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
					if (latch != null) {
						latch.countDown();
					}
				}
				catch (RemoteException e) {
					latch.countDown();
				}
			}}.start();
	}

	/**
	 * Frissíti a kliens egyenlegét.
	 * @param playerCommand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void refreshBalance(PlayerCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = userDAO.findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			if (playerCommand.getRaiseAmount() != null) {
				u.setBalance(u.getBalance().subtract(playerCommand.getRaiseAmount()));
			}
			userDAO.modify(u);
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
			throw new PokerUserBalanceException(ERR_BALANCE);
		}
		return true;
	}

	/**
	 * BLIND típusú utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedBlindPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		--whosOn;
	}

	/**
	 * CALL típusú utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedCallPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		++votedPlayers;
	}

	/**
	 * CHECK típusú utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 */
	protected abstract void receivedCheckPlayerCommand(PlayerCommand playerComand);

	/**
	 * RAISE típusú utasítás érkezett egy klienstől.
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedRaisePlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		votedPlayers = 1;
	}

	/**
	 * Ha a kliens üzenetét fogadtuk, akkor ezeket az utasításokat
	 * minden fogadott kliens utasítás után kötelezően végre kell hajtani.
	 * @param playerCommand az utasítás
	 */
	protected void endOfReceivedPlayerCommand(PlayerCommand playerCommand) {
		++whosOn;
		whosOn = (findNextValidClient(whosOn) % leftRoundMask.length);
		playerCommand.setWhosOn(whosOn);
		playerCommand.setClientsCount(clients.size());
		notifyClients(playerCommand);
		nextRound();
	}

	/**
	 * Új kört indít a szerveren.
	 */
	protected void startRound() {
		//ha a timertask == null, akkor nincs soron következő játékos, vagyis "idle" van
		if (clients.size() + waitingClients.size() >= minPlayer && timerTask == null) {
			prepareNewRound();
			preStartRound();
			collectBlinds();
			dealCardsToPlayers();
		}
	}

	/**
	 * Kártyalapokat oszt a játékosoknak.
	 */
	protected void dealCardsToPlayers() {
		int cardsToHand = pokerTable.getPokerType().getCardsToPlayers();
		for (int i = 0; i < clients.size(); i++) {
			Card[] cards = new Card[cardsToHand];
			IntStream.range(0, cardsToHand).forEach(j -> cards[j] = deck.popCard());
			users.get(i).setCards(cards);
			notifyNthClient(i, houseDealCommandFactory(cards));
		}
		nextStep();
	}

	/**
	 * Bekéri a vakokat a játékosoktól.
	 */
	protected void collectBlinds() {
		IntStream.range(0, clients.size()).forEach(i ->
			notifyNthClient(i, houseBlindCommandFactory(i, i, clients.size(), dealer, whosOn, users.stream().map(User::getUserName).collect(Collectors.toList())))
		);
		nextStep();
	}

	/**
	 * Elszámolja az asztalon lévő zsetonokat.
	 * @param houseCards a ház lapjai
	 */
	protected void bookMoneyStack(List<Card> houseCards) {
		try {
			int winnerIndex = getWinner(houseCards).keySet().iterator().next();
			User winnerUser = users.get(winnerIndex);
			User u = userDAO.findByUserName(winnerUser.getUserName());
			u.setBalance(u.getBalance().add(moneyStack));
			userDAO.modify(u);
			moneyStack = BigDecimal.ZERO;
		} catch (PokerDataBaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Megkeresi a nyertes játékost.
	 * @param houseCards a ház lapjai
	 * @return a nyertes kulcs-érték párja (kulcs=index, érték=kártyalapok)
	 */
	private HashMap<Integer, Card[]> getWinner(List<Card> houseCards) {
		if (houseCards == null) {
			houseCards = new ArrayList<>();
		}
		HashMap<Integer, Card[]> values = new HashMap<>();
		int winner = -1;
		List<User> playersInRound = new ArrayList<>();
		for (int i = 0; i < leftRoundMask.length; i++) {
			if (!leftRoundMask[i]) {
				playersInRound.add(users.get(i));
			}
		}
		List<IPlayer> winnerList = HoldemHandEvaluator.getInstance().getWinner(houseCards, playersInRound);
		Card[] cards = winnerList.get(0).getCards();
		for (int i = 0; i < users.size(); i++) {
			boolean gotIt = true;
			for (int j = 0; j < cards.length; j++) {
				if (!users.get(i).getCards()[j].equals(cards[j])) {
					gotIt = false;
					break;
				}
			}
			if (gotIt) {
				winner = i;
				break;
			}
		}
		values.put(winner, cards);
		return values;
	}

	/**
	 * DEAL típusú ház utasítást hoz létre.
	 * @param cards a kártyalapok
	 * @return az új utasítás
	 */
	protected abstract HouseCommand houseDealCommandFactory(Card[] cards);

	/**
	 * WINNER típusú ház utasítást hoz létre.
	 * @param cards a nyertes kártyalapok
	 * @param winner a nyertes
	 * @param whosOn ki következik
	 * @return az új utasítás
	 */
	protected abstract HouseCommand houseWinnerCommandFactory(Card[] cards, int winner, int whosOn);

	/**
	 * QUIT típusú players utasítást hoz létre.
	 * @param userName a kilépett kliens neve
	 * @return az új utasítás
	 */
	protected abstract PlayerCommand playerQuitCommandFactory(String userName);

	/**
	 * FOLD típusú player utasítást hoz létre.
	 * @param whosOn a soron következő játékos
	 * @return az új utasítás
	 */
	protected abstract PlayerCommand playerFoldCommandFactory(int whosOn);

	/**
	 * BLIND típusú ház utasítást hoz létre.
	 * @param fixSitPosition a kliens fix pozíciója a szervernél
	 * @param nthPlayer a körben hanyadik játékos a kliens
	 * @param players a játékosok darabszáma
	 * @param dealer az osztó sorszáma
	 * @param whosOn ki következik
	 * @param clientsNames az asztalnál ülő kliensek nevei
	 * @return az új utasítás
	 */
	protected abstract HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames);

	/**
	 * Következő lépés a szerveren.
	 */
	protected abstract void nextStep();

	/**
	 * A nyertes megkeresése, és kihirdetése.
	 * @return WINNER típusú utasítás
	 */
	protected HouseCommand winner() {
		HashMap<Integer, Card[]> winner = getWinner(houseCards);
		int winnerIndex = winner.keySet().iterator().next();
		return houseWinnerCommandFactory(winner.values().iterator().next(), winnerIndex, whosOn);
	}

	/**
	 * Következő kör a szerveren.
	 */
	protected abstract void nextRound();

	/**
	 * Utasítás érkezett egy klienstől
	 * @param client a kliens
	 * @param playerCommand az utasítás
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 */
	protected abstract void receivedPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException;

	/**
	 * Vadonatúj kör kezdetének előkészítése.
	 */
	protected abstract void prepareNewRound();

	/**
	 * Join esetén a két játéktábla-szerver közös része.
	 * @param client a kliens
	 * @param userName a kliens felhasználóneve
	 */
	protected void preJoin(PokerRemoteObserver client, String userName) {
		clients.add(client);
		clientsNames.add(userName);
		System.out.println("JOIN: " + client.toString());
	}

	/**
	 * Ha éppen folyik egy party az asztalnál, akkor várakozó listára kerül a frissen csatlakozott játékos.
	 * @param client a kliens
	 * @param userName a kliens felhasználóneve
	 */
	protected void waitingJoin(PokerRemoteObserver client, String userName) {
		waitingClients.add(client);
		waitingClientsNames.add(userName);
		System.out.println("WAITING: " + client.toString());
	}

	/**
	 * Van-e még hely az asztalnál.
	 * @return ha igen, akkor true, különben false
	 */
	public boolean canSitIn() {
		return (waitingClients.size() + clients.size()) < pokerTable.getMaxPlayers();
	}

	/**
	 * Elindítja az automatikus kiléptető feladatot.
	 * @param playerCommand az utasítás
	 */
	protected void startAutomateQuitTask() {
		System.out.println("startAutomateQuitTask");
		if (timerTask != null) {
			timerTask.cancel();
		}
		timer.purge();
		if (clients.size() > 1 && 1 < playersInRound && votedPlayers < playersInRound) {
			timerTask = createNewTimerTask();
			System.out.println("Időzítem a timertaskot!");
			timer.schedule(timerTask, pokerTable.getMaxTime() * 1000);
		} else {
			timerTask = null;
		}
	}

	/**
	 * Lekérdezi az asztalnál ülő játékosok számát.
	 * @return az asztalnál ülő játékosok darabszáma
	 */
	public int getPlayersCount() {
		return clients.size() + waitingClients.size();
	}

	public Integer getId() {
		return pokerTable.getId();
	}

	/**
	 * Ha játékstílust cserélünk egy asztalon.
	 * @param pokerTable az új asztal
	 */
	public void setPokerTable(PokerTable pokerTable) {
		this.pokerTable = pokerTable;
	}
}