package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
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
	protected List<RemoteObserver> clients;

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

	/**
	 * Hány játékos adott már le voksot az adott körben (raise-nél = 1).
	 */
	protected int votedPlayers;

	/**
	 * Legalább hány játékos kell, hogy elinduljon a játék.
	 * Asztaltól fogom lekérni.
	 */
	@Deprecated
	protected int minPlayer = 3;

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
				//ha elegen vagyunk az asztalnál, akkor indulhat a játék
				if (clients.size() >= minPlayer) {
					preStartRound();
					startRound();
				}
			}
		}
	}

	protected void preStartRound() {
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

	protected void sendPokerCommand(int i, PokerCommand pokerCommand) {
		System.out.println("spc, ertesitem a " + i + ". klienst!");
		printCommand(pokerCommand);
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

	protected void notifyClients(PokerCommand pokerCommand) {
		int i = 0;
		for (RemoteObserver pokerTableServerObserver : clients) {
			System.out.println("nc, ertesitem a " + i + ". klienst!");
			printCommand(pokerCommand);
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

	private void printCommand(PokerCommand pokerCommand) {
		if (pokerCommand instanceof HoldemHouseCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemHouseCommand)pokerCommand).getHouseCommandType());
		} else if (pokerCommand instanceof HoldemPlayerCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemPlayerCommand)pokerCommand).getPlayerCommandType());
		} else if (pokerCommand instanceof ClassicHouseCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemHouseCommand)pokerCommand).getHouseCommandType());
		} else if (pokerCommand instanceof ClassicPlayerCommand) {
			System.out.println("Utasitas típusa: " + ((HoldemHouseCommand)pokerCommand).getHouseCommandType());
		}
	}

	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	protected void refreshBalance(PlayerCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

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

	protected void receivedBlindPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		--whosOn;
	}

	protected void receivedCallPlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		++votedPlayers;
	}

	protected void receivedCheckPlayerCommand(PlayerCommand playerComand) {
		++votedPlayers;
	}

	protected void receivedFoldPlayerCommand(PlayerCommand playerComand) {
		//++votedPlayers;
		--playersInRound;
		players.remove(whosOn);
		--whosOn;
		++foldCounter;
		// mert aki nagyobb az ő sorszámánál, az lejjebb csúszik eggyel.
	}
	
	protected void receivedRaisePlayerCommand(PlayerCommand playerComand) throws PokerUserBalanceException, PokerDataBaseException {
		refreshBalance(playerComand);
		votedPlayers = 1;
	}
	
	protected void receivedQuitPlayerCommand(RemoteObserver client, PlayerCommand playerComand) {
		System.out.println("WhosQuit param: " + playerComand.getWhosQuit());
		System.out.println("Kliens visszakeresve: " + clients.indexOf(client));
		clients.remove(client);
		//				++votedPlayers;
		--playersInRound;
		--whosOn;
	}
	
	protected void endOfReceivePlayerCommand(PlayerCommand playerComand) throws RemoteException {
		++whosOn;
		whosOn %= playersInRound;
		playerComand.setWhosOn(whosOn);
		notifyClients(playerComand);

		nextRound();
	}

	protected abstract void startRound();

	protected abstract void nextStep();

	protected abstract void winner(HouseCommand houseCommand);

	protected abstract void dealCardsToPlayers(int cardCount);

	protected abstract void collectBlinds();

	protected abstract void nextRound() throws RemoteException;

	protected abstract void receivePlayerCommand(RemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
}
