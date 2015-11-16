package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;

/**
 * A holdem játékmód kliens oldali controllere.
 * @author feher
 *
 */
public class HoldemMainGameModel extends AbstractMainGameModel {
	
	public HoldemMainGameModel(CommunicatorController communicatorController) {
		super(communicatorController);
	}


	@Override
	protected void tossBlind(Boolean bigBlind) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(bigBlind ? 1 : 2));
		myDebt = myDebt.subtract(amount);
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpBlindCommand(amount);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendCallCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		BigDecimal amount = BigDecimal.ZERO.add(myDebt);
		myDebt = myDebt.subtract(amount);
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpCallCommand(amount);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendCheckCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpCheckCommand();
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendRaiseCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpRaiseCommand(myDebt, pokerTable.getDefaultPot());
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendFoldCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpFoldCommand(youAreNth);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendQuitCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		HoldemPlayerCommand playerCommand = new HoldemPlayerCommand();
		playerCommand.setUpQuitCommand(youAreNth);
		sendCommandToTable(playerCommand);		
	}
}
