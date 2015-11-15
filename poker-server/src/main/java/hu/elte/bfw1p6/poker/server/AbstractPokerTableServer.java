package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;

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

/**
 * A póker játékasztal-szerverek absztrakciója.
 * @author feher
 *
 */
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
	 * A játékosok száma, akik eldobták a lapjaikat.
	 */
	protected int foldCounter;

	/**
	 * A felhasználók adatainak módosítására szolgáló objektum.
	 */
	protected UserDAO userDAO;

	protected AbstractPokerTableServer(PokerTable pokerTable) throws RemoteException, PokerDataBaseException {
		super();
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		this.clients = new ArrayList<>();
		this.moneyStack = BigDecimal.ZERO;
		this.players = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
		this.userDAO = new UserDAO();
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
			}
			clients.add(client);
			clientsNames.add(userName);
			System.out.println("JOIN: " + client.toString());
			startRound();
		}
	}

	/**
	 * QUIT típusú utasítás érkezett egy klienstől.
	 * @param client a kilépendő kliens
	 * @param playerComand az utasítás
	 * @throws PokerUserBalanceException
	 * @throws PokerDataBaseException
	 */
	protected void receivedQuitPlayerCommand(PokerRemoteObserver client, PlayerCommand playerComand) {
		// TODO: lehet, hogy itt a client paraméter lehagyható, elég ha nevet küld, vagy sorszámot...
		//TODO: lapjait is szedjük ki
		int index = clients.indexOf(client);
		System.out.println("WhosQuit param: " + playerComand.getWhosQuit());
		System.out.println("Kliens visszakeresve: " + clients.indexOf(client));
		clients.remove(index);
		if (players.size() > 0) {
			players.remove(index);
		}
		clientsNames.remove(index);
		//				++votedPlayers;
		if (playersInRound > 0) {
			--playersInRound;
		}
		--whosOn;
	}

	/**
	 * Inicializáció metódus új kör kezdés esetére.
	 */
	protected void preStartRound() {
		//még senki sem dobta el a lapjait
		foldCounter = 0;
		// megnézem, hogy aktuálisan hány játékos van az asztalnál
		playersInRound = clients.size();
		//következő játékos a dealer
		++dealer;
		//a kártyapaklit megkeverjük
		deck.reset();
		//senki sem beszélt még
		votedPlayers = 0;
		if (playersInRound > 0) {
			//nem baj, ha körbeértünk...
			dealer %= playersInRound;
			//a dealertől balra ülő harmadik játékos kezd
			whosOn = (dealer + 3) % playersInRound;
		}
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
					// TODO: megszakadt a klienssel a kapcsolat, tehát olyan mintha QUIT utasítást küldött volna...
					// ki kell szedni a clientsből, körbe kell küldeni, hogy ez a player QUIT-telt.
					e.printStackTrace();
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
	 * FOLD típusú utasítás érkezett egy klienstől.
	 */
	protected void receivedFoldPlayerCommand() {
		--playersInRound;
		players.remove(whosOn);
//		--whosOn;
		++foldCounter;
	}

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
		System.out.println(whosOn);
		++whosOn;
		if (playersInRound > 0)
			whosOn %= playersInRound;
		playerComand.setWhosOn(whosOn);
		playerComand.setClientsCount(clients.size());
		notifyClients(playerComand);
		nextRound();
	}

	/**
	 * Új kört indít a szerveren.
	 */
	protected void startRound() {
		if (clients.size() >= minPlayer) {
			preStartRound();
			prepareNewRound();
			IntStream.range(0, clients.size()).forEach(i ->
				players.add(new PokerPlayer(clientsNames.get(i)))
			);
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
	 * DEAL típusú ház utasítást hoz létre.
	 * @param cards a kártyalapok
	 * @return
	 */
	protected abstract HouseCommand houseDealCommandFactory(Card[] cards);

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
	 */
	protected abstract void winner(HouseCommand houseCommand);

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
}