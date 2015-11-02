package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.HousePokerCommand;
import hu.elte.bfw1p6.poker.command.PlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.type.api.HousePokerCommandType;
import hu.elte.bfw1p6.poker.command.type.api.PlayerPokerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public abstract class AbstractPokerClientModel<HPCT extends HousePokerCommandType<HPCT>, HPC extends HousePokerCommand<HPCT>, PPCT extends PlayerPokerCommandType<PPCT>, PPC extends PlayerPokerCommand<PPCT>> {

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

	public void receivedQuitPlayerCommand(HoldemPlayerPokerCommand playerHoldemCommand) {
		if (youAreNth > playerHoldemCommand.getWhosQuit()) {
			--youAreNth;
		}
	}
	
	public abstract void sendCommandToTable(PlayerPokerCommand<PPCT> playerPokerCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;

//	public void setMyDebt(BigDecimal myDebt) {
//		this.myDebt = myDebt;
//	}
	
//	public PokerPlayer getPlayer() {
//		return pokerSession.getPlayer();
//}
}
