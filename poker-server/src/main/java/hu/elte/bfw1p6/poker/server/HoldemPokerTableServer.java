package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
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
public class HoldemPokerTableServer {
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
	private List<PokerTableServerObserver> clients;
	
	private Deck deck;
	
	private House house;
	
	/**
	 * valahogy kéne Decket nyilvan tartani inteket küldök át, és simán filename alapján visszakeresik maguknak a kliensek...
	 * @param pokerTable
	 */
	
	public HoldemPokerTableServer(PokerTable pokerTable) {
		this.pokerTable = pokerTable;
		deck = new Deck();
		house = new House();
		// a játékosoknak osztok először lapokat
		this.actualHoldemHouseCommandType = HoldemHouseCommandType.PLAYER;
	}
	
	public void join(PokerTableServerObserver client) throws PokerTooMuchPlayerException {
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
	}
	
	private void dealCardsToPlayers() {
		for (PokerTableServerObserver pokerTableServerObserver : clients) {
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard());
			pokerTableServerObserver.updateClient(pokerCommand);
		}
	}
	
	private void flop() {
		
	}
	
	private void turn() {
		
	}
	
	private void river() {
		
	}
	
	private void notifyClients(PokerCommand pokerCommand) {
		for (PokerTableServerObserver pokerTableServerObserver : clients) {
			pokerTableServerObserver.updateClient(pokerCommand);
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
