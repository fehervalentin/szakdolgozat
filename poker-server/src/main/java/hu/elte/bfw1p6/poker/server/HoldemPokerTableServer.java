package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
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
	
	private Deck deck;
	
	private House house;
	
	/**
	 * valahogy kéne Decket nyilvan tartani inteket küldök át, és simán filename alapján visszakeresik maguknak a kliensek...
	 * @param pokerTable
	 */
	
	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		deck = new Deck();
		house = new House();
		clients = new ArrayList<>();
		// a játékosoknak osztok először lapokat
		this.actualHoldemHouseCommandType = HoldemHouseCommandType.PLAYER;
	}
	
	public void join(RemoteObserver client) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException("Az asztal betelt, nem tudsz csatlakozni!");
			} else {
				clients.add(client);
				if (clients.size() > 1) {
					dealCardsToPlayers();
					// megkezdődik az első kör (flop előtt) kivak nagyvak etc...
					// nézni kell, hogy hol tart a kör, ha körbeértünk, akkor mehet a flop, vagy adott esetben újabb körök!!!
					// pl valaki emel...
				}
			}
		}
		//dealCardsToPlayers();
	}
	
	private void dealCardsToPlayers() {
		for (RemoteObserver pokerTableServerObserver : clients) {
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard());
//			pokerTableServerObserver.updateClient(pokerCommand);
			try {
				pokerTableServerObserver.update(this, pokerCommand);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void flop() {
		
	}
	
	private void turn() {
		
	}
	
	private void river() {
		
	}
	
	private void notifyClients(PokerCommand pokerCommand) {
		for (RemoteObserver pokerTableServerObserver : clients) {
			try {
				pokerTableServerObserver.update(this, pokerCommand);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	public void sendPlayerCommand(PokerTableServerObserver client, PlayerCommand playerCommand) {
		if (clients.contains(client)) {
			// feldolgozzuk a kérését
			// majd minden observert értesítünk
			// jöhet a következő játékos
			// valahogyan időt is kéne mérni...
		}
	}
}
