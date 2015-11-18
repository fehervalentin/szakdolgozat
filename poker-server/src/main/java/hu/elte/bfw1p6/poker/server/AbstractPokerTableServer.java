package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

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
	 * Az automatikus kiléptetési feladat időzítője.
	 */
	private Timer timer;
	
	/**
	 * Az automatikus kiléptető feladat.
	 */
	private TimerTask timerTask;
	
	/**
	 * A kliensek, akik várakoznak a következő partyra (körre).
	 */
	protected List<PokerRemoteObserver> waitingClients;
	
	/**
	 * A kliensek nevei, akik várakoznak a következő partyra (körre).
	 */
	protected List<String> waitingClientsNames;
	
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
	 * Maguk a játékosok.
	 */
	protected List<PokerPlayer> players;
	
	/**
	 * A kliensek username-jei (mert a PokerPlayerben a userName-re nincs setter! (perzisztálást védi...)
	 */
	protected List<String> clientsNames;

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
		this.moneyStack = BigDecimal.ZERO;
		this.players = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
		this.userDAO = new UserDAO();
		this.waitingClients = new ArrayList<>();
		this.waitingClientsNames = new ArrayList<>();
		this.timer = new Timer();
	}
	
	protected TimerTask createNewTimerTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
//				PlayerCommand playerCommand = playerQuitCommandFactory(clientsNames.get(whosOn));
				PlayerCommand playerCommand = playerQuitCommandFactory("");
				//TODO: Exception in thread "Timer-0" java.lang.ArrayIndexOutOfBoundsException: -1
//				at java.util.ArrayList.elementData(ArrayList.java:418)
//				at java.util.ArrayList.get(ArrayList.java:431)
//				at hu.elte.bfw1p6.poker.server.AbstractPokerTableServer$1.run(AbstractPokerTableServer.java:157)
//				at java.util.TimerThread.mainLoop(Timer.java:555)
//				at java.util.TimerThread.run(Timer.java:505)
				receivedQuitPlayerCommand(clients.get(whosOn), playerCommand);
				endOfReceivedPlayerCommand(playerCommand);
			}
		};
	}
	
	
	protected int findNextValidClient(int whosOn) {
		int start = whosOn; // TODO: NA VAJON EZ KELL?
		//TODO: ha körbeértünk, vagy már csak 1 kliens van, akkor reset game...
		whosOn %= leftRoundMask.length;
		while (leftRoundMask[whosOn]) {
			++whosOn;
			whosOn %= leftRoundMask.length;
		}
		return whosOn;
	}


	/**
	 * Asztalhoz való csatlakozás.
	 * @param client a csatlakozni kívánó kliens
	 * @param userName a csatlakozni kívánó játékos neve
	 * @throws PokerTooMuchPlayerException
	 */
	public abstract void join(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException;

	/**
	 * QUIT típusú utasítás érkezett egy klienstől.
	 * @param client a kilépendő kliens
	 * @param playerCommand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedQuitPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) {
		System.out.println("WhosQuit param: " + playerCommand.getWhosQuit());
		System.out.println("Kliens visszakeresve: " + clients.indexOf(client));
		int index = clients.indexOf(client);
		leftRoundMask[index] = true;
		clients.remove(index);
		clientsNames.remove(index);
		--playersInRound;
		--whosOn;
		try {
			client.update(playerCommand);
		} catch (RemoteException e) {
			removeClient(index);
		}
		prepareNewRound();
	}
	
	protected void receivedQuitPlayerCommandFromWaitingPlayer(PokerRemoteObserver client) {
		System.out.println("WAITING PLAYER LEFT: " + client);
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
		players.clear();
		//új játékosokat veszek fel
		IntStream.range(0, clients.size()).forEach(i -> players.add(new PokerPlayer(clientsNames.get(i))));
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
				} catch (RemoteException e) {
					removeClient(i);
				}
			}}.start();
	}
	
	/**
	 * Törli az i. klienst
	 * @param i az index
	 */
	private void removeClient(int i) {
		PlayerCommand playerCommand = playerQuitCommandFactory(clientsNames.get(i));
		receivedQuitPlayerCommand(clients.get(i), playerCommand);
		notifyClients(playerCommand);
		endOfReceivedPlayerCommand(playerCommand);
	}

	/**
	 * Az összes klienst értesíti.
	 * @param pokerCommand az utasítás
	 */
	protected void notifyClients(PokerCommand pokerCommand) {
		IntStream.range(0, clients.size()).forEach(i -> notifyNthClient(i, pokerCommand));
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
	 * @throws RemoteException
	 */
	protected void endOfReceivedPlayerCommand(PlayerCommand playerCommand) {
		if (playersInRound > 0) {
			++whosOn;
			whosOn = (findNextValidClient(whosOn) % leftRoundMask.length);
			playerCommand.setWhosOn(whosOn);
			playerCommand.setClientsCount(clients.size());
			notifyClients(playerCommand);
			nextRound();
		}
	}

	/**
	 * Új kört indít a szerveren.
	 */
	protected void startRound() {
		if (clients.size() + waitingClients.size() >= minPlayer) {
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
			players.get(i).setCards(cards);
			notifyNthClient(i, houseDealCommandFactory(cards));
		}
		nextStep();
	}

	/**
	 * Bekéri a vakokat a játékosoktól.
	 */
	protected void collectBlinds() {
		System.out.println("Kliensek szama: " + players.size());
		IntStream.range(0, clients.size()).forEach(i ->
			notifyNthClient(i, houseBlindCommandFactory(i, i, clients.size(), dealer, whosOn, players.stream().map(PokerPlayer::getUserName).collect(Collectors.toList()))
		));
		nextStep();
	}
	
	/**
	 * Elszámolja az asztalon lévő zsetonokat.
	 * @param houseCards a ház lapjai
	 */
	protected void bookMoneyStack(List<Card> houseCards) {
		try {
			int winnerIndex = getWinner(houseCards).keySet().iterator().next();
			PokerPlayer winnerPlayer = players.get(winnerIndex);
			User u = userDAO.findByUserName(winnerPlayer.getUserName());
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
		List<PokerPlayer> playersInRound = new ArrayList<>();
		for (int i = 0; i < leftRoundMask.length; i++) {
			if (!leftRoundMask[i]) {
				playersInRound.add(players.get(i));
			}
		}
		List<IPlayer> winnerList = HoldemHandEvaluator.getInstance().getWinner(houseCards, playersInRound);
		Card[] cards = winnerList.get(0).getCards();
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
	 * @param houseCommand az utasítás
	 */
	protected HouseCommand winner() {
		HashMap<Integer, Card[]> winner = getWinner(houseCards);
		int winnerIndex = winner.keySet().iterator().next();
		long count = IntStream.range(0, leftRoundMask.length).filter(i -> leftRoundMask[i]).count();
		winnerIndex += count;
		winnerIndex %= leftRoundMask.length;
//		System.out.println("Hányan dobták a lapjaikat: " + count);
//		System.out.println("A győztes sorszáma: " + winnerIndex);
//		System.out.println("A győztes kártyalapjai: " + Arrays.toString(winner.values().iterator().next()));
		return houseWinnerCommandFactory(winner.get(winnerIndex), winnerIndex, whosOn);
	}

	/**
	 * Következő kör a szerveren.
	 * @throws RemoteException
	 */
	protected abstract void nextRound();

	/**
	 * Utasítás érkezett egy klienstől
	 * @param client a kliens
	 * @param playerCommand az utasítás
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	protected abstract void receivedPlayerCommand(PokerRemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException;

	/**
	 * Vadonatúj kör kezdetének előkészítése.
	 */
	protected abstract void prepareNewRound();
	
	/**
	 * Join esetén a két játéktábla-szerver közös része.
	 * @param client a kliens
	 * @param userName a kliens felhasználóneve
	 * @throws PokerTooMuchPlayerException
	 */
	protected void preJoin(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException {
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
	protected void startAutomateQuitTask(PlayerCommand playerCommand) {
		if (timerTask != null) {
			timerTask.cancel();
		}
		timer.purge();
		endOfReceivedPlayerCommand(playerCommand);
		timerTask = createNewTimerTask();
		timer.schedule(timerTask, pokerTable.getMaxTime() * 1000);
	}
	
	/**
	 * Lekérdezi az asztalnál ülő játékosok számát.
	 * @return az asztalnál ülő játékosok darabszáma
	 */
	public int getPlayersCount() {
		return clients.size() + waitingClients.size();
	}
}