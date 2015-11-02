package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerPokerCommandType;
import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public abstract class AbstractPokerClientModel<T extends PokerCommandType<T>, E extends PokerCommandType<E>> {

	protected PokerSession pokerSession;
	protected PokerRemote pokerRemote;

	protected PokerTable pokerTable;
	
	protected CommunicatorController communicatorController;

	/**
	 * Hanyadik vagyok a körben.
	 */
	protected int youAreNth;

	/**
	 * Hány játékos van velem együtt.
	 */
	protected int players;

	/**
	 * A tartozásom az asztal felé, amit <b>CALL</b> vagy <b>RAISE</b> esetén meg kell adnom.
	 */
	protected BigDecimal myDebt;
	
	public AbstractPokerClientModel(CommunicatorController communicatorController) {
		this.pokerSession = Model.getInstance().getPokerSession();
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.youAreNth = -1;
		this.pokerTable = ConnectTableHelper.getInstance().getPokerTable();
		this.myDebt = pokerTable.getDefaultPot();
		this.communicatorController = communicatorController;

		System.out.println("Ki vagyok: " + getUserName());
	}
	
	public void connectToTable(RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		pokerRemote.connectToTable(pokerSession.getId(), pokerTable, observer);
	}

	public String getUserName() {
		return pokerSession.getPlayer().getUserName();
	}

	public BigDecimal getMyDebt() {
		return myDebt;
	}

	public int getYouAreNth() {
		return youAreNth;
	}

	public void receivedQuitPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		if (youAreNth > playerHoldemCommand.getWhosQuit()) {
			--youAreNth;
		}
	}
	
	public abstract void sendCommandToTable(PlayerCommand<E> playerHoldemCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;

//	public void setMyDebt(BigDecimal myDebt) {
//		this.myDebt = myDebt;
//	}
	
//	public PokerPlayer getPlayer() {
//		return pokerSession.getPlayer();
//}
}
