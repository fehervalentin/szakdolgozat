package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.type.ClassicHouseCommandType;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ClassicPokerTableServer extends AbstractPokerTableServer<ClassicHouseCommandType> {

	private static final long serialVersionUID = 804318360089503038L;
	
	private ClassicHouseCommandType actualHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
	}

	@Override
	protected void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
			prepareNextRound();
			//a vakokat kérem be legelőször
			//actualHoldemHouseCommandType = HoldemHouseCommandType.BLIND;
//			actualHouseCommandType = ClassicHouseCommandType.values()[0];
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers();
		}
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

	@Override
	protected HouseCommand<ClassicHouseCommandType> getNewCommand() {
		return new ClassicHouseCommand();
	}

}
