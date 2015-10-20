package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.server.logic.Deck;
import hu.elte.bfw1p6.poker.server.logic.House;

/**
 * Maga a póker asztal megvalósítása
 * @author feher
 *
 */
public class HoldemPokerTableServer extends UnicastRemoteObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";

	/**
	 * Maga az asztal entitás, le lehet kérni mindent...
	 */
	private PokerTable pokerTable;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben)
	 */
	private HoldemHouseCommandType actualHoldemHouseCommandType;

	/**
	 * Maga a pénz stack
	 */
	private BigDecimal stack;

	/**
	 * Kliensek (observerek)
	 */
	private List<RemoteObserver> clients;

	/**
	 * Kártyapakli
	 */
	private Deck deck;

	private House house;

	/**
	 * Hány játékos játszik az adott körben
	 */
	private int playersInRound;

	/**
	 * Ki az osztó az adott körben
	 */
	private int dealer = -1;

	/**
	 * Ki van soron éppen
	 */
	private int whosOn;

	/**
	 * Hány játékos adott már le voksot az adott körben (raise-nél = 1)
	 */
	private int votedPlayers;

	//	private BigDecimal actualRaise;

	private int minPlayer = 3;

	/**
	 * valahogy kéne Decket nyilvan tartani inteket küldök át, és simán filename alapján visszakeresik maguknak a kliensek...
	 * @param pokerTable
	 */

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		deck = new Deck();
		house = new House();
		clients = new ArrayList<>();
		// a vakokat kérem be legelőször
		this.actualHoldemHouseCommandType = HoldemHouseCommandType.values()[0];
	}

	public synchronized int join(RemoteObserver client) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException("Az asztal betelt, nem tudsz csatlakozni!");
			} else {
				clients.add(client);
				startRound();
			}
		}
		return clients.size();
	}

	private void startRound() {
		if (clients.size() >= minPlayer) {
			playersInRound = clients.size();
			++dealer;
			dealer %= playersInRound;
			deck.reset();
			votedPlayers = 0;
			whosOn = (dealer + 3) % playersInRound;
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers();
		}
	}

	private void collectBlinds() {
		//		actualRaise = pokerTable.getDefaultPot();
		for (int i = 0; i < clients.size(); i++) {
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, i, clients.size(), dealer, whosOn);
			try {
				clients.get(i).update(null, pokerCommand);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sendPokerCommand(j, pokerCommand);
		}
		nextStep();
	}

	private void sendPokerCommand(int j, PokerCommand pokerCommand) {
		new Thread() {

			@Override
			public void run() {
				try {
					clients.get(j).update(null, pokerCommand);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}.start();
	}

	private void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			int j = i;
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard(), whosOn);
			sendPokerCommand(j, pokerCommand);
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
						pokerTableServerObserver.update(null, pokerCommand);
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
				++votedPlayers;
				--playersInRound;
				break;
			}
			case RAISE: {
				refreshBalance(playerCommand);
				votedPlayers = 1;
				break;
			}
			case QUIT: {
				clients.remove(client);
				++votedPlayers;
				--playersInRound;
				break;
			}
			default:
				break;
			}
			++whosOn;
			whosOn %= playersInRound;
			playerCommand.setWhosOn(whosOn);
			notifyClients(playerCommand);
			// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
			if (actualHoldemHouseCommandType == HoldemHouseCommandType.BLIND && votedPlayers >= playersInRound) {
				// itt kell eldönteni, hogy ki nyert, és azt körbe is kell ám küldeni!
				System.out.println("új kör");
				startRound();
			} else {
				// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
				if (votedPlayers >= playersInRound) {
					PokerCommand pokerCommand = null;
					switch (actualHoldemHouseCommandType) {
					case FLOP: {
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard(), deck.popCard(), whosOn);
						break;
					}
					case TURN: {
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), whosOn);
						break;
					}
					case RIVER: {
						pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), whosOn);
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
		if (playerCommand.getRaiseAmount() != null) {
			newBalance = newBalance.subtract(playerCommand.getRaiseAmount());
		}
		if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new PokerUserBalanceException(ERR_BALANCE_MSG);
		}
		return true;
	}
}