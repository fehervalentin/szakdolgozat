package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
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
	
	//TODO: kell egy időzítő!!!
	// ha egy kliens jön éppen,, akkor kommunikáció áll, de kell egy timeout limit,
	//amit az asztal határoz meg, ha addig nem jön válasz, akkor kiléptetjük a felhasználót
	//a timeoutnak kell egy kis dilatáció, hogy a kliens oldali timer tudjon reagálni

	private static final long serialVersionUID = -2646114665508361840L;

	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";
	protected final String ERR_TABLE_FULL = "Az asztal betelt, nem tudsz csatlakozni!";
	
	protected List<PokerRemoteObserver> waitingClients;
	
	protected List<String> waitingClientsNames;
	
	/**
	 * Ház lapjai. Classic esetében null marad.
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
	 * A felhasználók adatainak módosítására szolgáló objektum.
	 */
	protected UserDAO userDAO;
	
	protected boolean[] foldMask = new boolean[6];
	protected boolean[] quitMask = new boolean[6];

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
	}
	
	
	protected int findNextValidClient(int whosOn) {
		int start = whosOn;
		//TODO: ha körbeértünk, vagy már csak 1 kliens van, akkor reset game...
		whosOn %= foldMask.length;
		while (foldMask[whosOn] || quitMask[whosOn]) {
			++whosOn;
			whosOn %= foldMask.length;
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
		quitMask[index] = true;
		clients.remove(index);
		clientsNames.remove(index);
		--playersInRound;
		--whosOn;
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
		foldMask[whosOn] = true;
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
		foldMask = new boolean[playersInRound];
		quitMask = new boolean[playersInRound];
		//következő játékos a dealer
		++dealer;
		//a kártyapaklit megkeverjük
		deck.reset();
		//senki sem beszélt még
		votedPlayers = 0;
		if (playersInRound > 0) {
			//nem baj, ha körbeértünk...
			dealer %= foldMask.length;
			//a dealertől balra ülő harmadik játékos kezd
			whosOn = (dealer + 3) % playersInRound;
		}
		//törlöm a játékosokat
		players.clear();
		
		IntStream.range(0, clients.size()).forEach(i -> players.add(new PokerPlayer(clientsNames.get(i)))
	);
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
					PlayerCommand playerCommand;
					switch (pokerTable.getPokerType()) {
					case HOLDEM: {
						playerCommand = new HoldemPlayerCommand().setUpQuitCommand(i);
						break;
					}
					case CLASSIC: {
						playerCommand = new ClassicPlayerCommand().setUpQuitCommand(i);
						break;
					}
					default: {
						throw new IllegalArgumentException();
					}
					}
					playerCommand.setSender(clientsNames.get(i));
					playerCommand.setClientsCount(clients.size()-1);
					receivedQuitPlayerCommand(clients.get(i), playerCommand);
					notifyClients(playerCommand);
					try {
						endOfReceivedPlayerCommand(playerCommand);
					} catch (RemoteException e1) {
						//TODO: GEEEEEEEEZ
						e1.printStackTrace();
					}
				}
			}}.start();
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
			throw new PokerUserBalanceException(ERR_BALANCE_MSG);
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
	 * Ha a kliens üzenetét feldolgoztuk, akkor ezeket az utasításokat
	 * minden kliens utasítás után kötelezően végre kell hajtani.
	 * @param playerComand az utasítás
	 * @throws RemoteException
	 */
	protected void endOfReceivedPlayerCommand(PlayerCommand playerComand) throws RemoteException {
		if (playersInRound > 0) {
			System.out.println(whosOn);
			++whosOn;
			whosOn = findNextValidClient(whosOn);
			whosOn %= foldMask.length;
			playerComand.setWhosOn(whosOn);
			playerComand.setClientsCount(clients.size());
			notifyClients(playerComand);
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
	
	protected void bookMoneyStack(List<Card> houseCards) {
		try {
			int winnerIndex = getWinner(houseCards).keySet().iterator().next();
			PokerPlayer winnerPlayer = players.get(winnerIndex);
			User u = userDAO.findByUserName(winnerPlayer.getUserName());
			u.setBalance(u.getBalance().add(moneyStack));
			userDAO.modify(u);
			moneyStack = BigDecimal.ZERO;
		} catch (PokerDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer, Card[]> getWinner(List<Card> houseCards) {
		if (houseCards == null) {
			houseCards = new ArrayList<>();
		}
		HashMap<Integer, Card[]> values = new HashMap<>();
		int winner = -1;
		List<PokerPlayer> valami = new ArrayList<>();
		for (int i = 0; i < foldMask.length; i++) {
			if (!foldMask[i] && ! quitMask[i]) {
				valami.add(players.get(i));
			}
		}
		List<IPlayer> winnerList = HoldemHandEvaluator.getInstance().getWinner(houseCards, players);
		Card[] cards = winnerList.get(0).getCards();
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
		values.put(winner, cards);
		return values;
	}

	/**
	 * DEAL típusú ház utasítást hoz létre.
	 * @param cards a kártyalapok
	 * @return
	 */
	protected abstract HouseCommand houseDealCommandFactory(Card[] cards);
	
	protected abstract HouseCommand houseWinnerCommandFactory(Card[] cards, int winner, int whosOn);

	/**
	 * BLIND típusú ház utasítást hoz létre.
	 * @param fixSitPosition a kliens fix pozíciója a szervernél
	 * @param nthPlayer a körben hanyadik játékos a kliens
	 * @param players a játékosok darabszáma
	 * @param dealer az osztó sorszáma
	 * @param whosOn ki következik
	 * @param clientsNames az asztalnál ülő kliensek nevei
	 * @return
	 */
	protected abstract HouseCommand houseBlindCommandFactory(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames);

	/**
	 * Következő lépés a szerveren.
	 */
	protected abstract void nextStep();

	/**
	 * A nyertes megkeresése, és kihirdetése.
	 * @param houseCommand az utasítás
	 * @throws PokerDataBaseException 
	 */
	protected HouseCommand winner() {
		HashMap<Integer, Card[]> winner = getWinner(houseCards);
		int winnerIndex = winner.keySet().iterator().next();
		long count = IntStream.range(0, foldMask.length).filter(i -> foldMask[i]).count();
		long count2 = IntStream.range(0, quitMask.length).filter(i -> foldMask[i]).count();
		winnerIndex += (count + count2);
		winnerIndex %= foldMask.length;
		System.out.println("Hányan dobták a lapjaikat: " + count);
		System.out.println("A győztes sorszáma: " + winnerIndex);
		System.out.println("A győztes kártyalapjai: " + Arrays.toString(winner.values().iterator().next()));
		return houseWinnerCommandFactory(winner.get(winnerIndex), winnerIndex, whosOn);
	}

	/**
	 * Következő kör a szerveren.
	 * @throws RemoteException
	 */
	protected abstract void nextRound() throws RemoteException;

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
	
	protected void preJoin(PokerRemoteObserver client, String userName) throws PokerTooMuchPlayerException {
		clients.add(client);
		clientsNames.add(userName);
		System.out.println("JOIN: " + client.toString());
	}
	
	protected void waitingJoin(PokerRemoteObserver client, String userName) {
		waitingClients.add(client);
		waitingClientsNames.add(userName);
		System.out.println("WAITING: " + client.toString());
	}
	
	public boolean canSitIn() {
		return (waitingClients.size() + clients.size()) < pokerTable.getMaxPlayers();
	}
}