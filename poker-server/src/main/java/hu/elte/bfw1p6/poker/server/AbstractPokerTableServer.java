package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.CommandType;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.server.logic.Deck;

public abstract class AbstractPokerTableServer<T extends CommandType<T>> extends UnicastRemoteObject {

	private static final long serialVersionUID = 1954723026118781134L;
	
	private final String ERR_TABLE_FULL = "Az asztal betelt, nem tudsz csatlakozni!";
	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";
	
	/**
	 * Épp milyen utasítást fog kiadni a szerver.
	 */
//	protected CommandType<T> actualHouseCommandType;

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

	protected int foldCounter;
	
	protected AbstractPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		this.deck = new Deck();
		this.clients = new ArrayList<>();
		this.moneyStack = BigDecimal.ZERO;
		this.players = new ArrayList<>();
		this.clientsNames = new ArrayList<>();
//		this.actualHouseCommandType = actualHouseCommandType.getValues()[0];
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
			}
			clients.add(client);
			clientsNames.add(userName);
			System.out.println("JOIN: " + client.toString());
			startRound();
		}
	}
	
	protected void prepareNextRound() {
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
	

	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}
	

	public void refreshBalance(HoldemPlayerCommand playerCommand) throws PokerUserBalanceException, PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	public boolean isThereEnoughMoney(User u, HoldemPlayerCommand playerCommand) throws PokerUserBalanceException {
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
	

	protected void collectBlinds() {
		for (int i = 0; i < clients.size(); i++) {
			HoldemHouseCommand houseHoldemCommand = new HoldemHouseCommand();
			houseHoldemCommand.setUpBlindCommand(i, clients.size(), dealer, whosOn, clientsNames);
			sendPokerCommand(i, houseHoldemCommand);
		}
		nextStep();
	}
	
	/**
	 * Egy adott kliensnek küld utasítást.
	 * @param i a kliens sorszáma
	 * @param pokerCommand az utasítást
	 */
	protected void sendPokerCommand(int i, PokerCommand pokerCommand) {
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
	 * Minden kliensek elküldi az utasítást.
	 * @param pokerCommand az utasítás
	 */
	protected void notifyClients(PokerCommand pokerCommand) {
		for (int i = 0; i < clients.size(); i++) {
			sendPokerCommand(i, pokerCommand);
		}
	}
	
	protected abstract void startRound();
	
	protected abstract void nextStep();
	
	protected abstract void nextRound() throws RemoteException;
	
	protected abstract void winner(HouseCommand houseCommand);

	protected void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			Card[] cards = new Card[]{deck.popCard(), deck.popCard()};
			PokerPlayer pokerPlayer = new PokerPlayer();
			pokerPlayer.setCards(cards);
			players.add(pokerPlayer);
			HouseCommand<T> houseCommand = getNewCommand();
			houseCommand.setUpDealCommand(cards, whosOn);
			sendPokerCommand(i, houseCommand);
		}
		nextStep();
	}
	
	protected abstract HouseCommand<T> getNewCommand();
	
//	protected abstract HouseCommand<T> getNewCommandType();

}
