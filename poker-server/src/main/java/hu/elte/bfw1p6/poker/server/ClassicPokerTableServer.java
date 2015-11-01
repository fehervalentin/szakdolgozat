package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.type.ClassicHouseCommandType;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ClassicPokerTableServer extends AbstractPokerTableServer {

	private static final long serialVersionUID = 804318360089503038L;

	/**
	 * Épp milyen utasítást fog kiadni a szerver.
	 */
	private ClassicHouseCommandType actualHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
			prepareNextRound();
			//a vakokat kérem be legelőször
			//actualHoldemHouseCommandType = HoldemHouseCommandType.BLIND;
			actualHouseCommandType = ClassicHouseCommandType.values()[0];
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers();
		}
	}
	
	private void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			Card c1 = deck.popCard();
			Card c2 = deck.popCard();
			Card c3 = deck.popCard();
			Card c4 = deck.popCard();
			Card c5 = deck.popCard();
			PokerPlayer pokerPlayer = new PokerPlayer();
			pokerPlayer.setCards(new Card[]{c1, c2, c3, c4, c5});
			players.add(pokerPlayer);
			ClassicHouseCommand pokerCommand = new ClassicHouseCommand();
			pokerCommand.setUpPlayerCommand(c1, c2, whosOn);
			sendPokerCommand(i, pokerCommand);
		}
		nextStep();
	}

	@Override
	protected void nextStep() {
		actualHouseCommandType = actualHouseCommandType.getNext();
	}

	@Override
	protected void nextRound() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void winner(HouseCommand houseCommand) {
		// TODO Auto-generated method stub

	}

}
